<?php

if (!defined("ULib"))
{
	die("非法访问！");
}

function people_get_people_type($card_no)
{
	global $udb;
	$udb -> query("SELECT * FROM `ulib_users`
			WHERE `card_no` = '{$card_no}';");
	$result = $udb ->fetch_assoc();
	// 如果没有这个人的话会返回空
	return $result["usertype"];
}

/*
 * 获取某人借书的列表 array (
 * 							array(prop_no, behalf_no, borrow_time, 是否超期, book_name)
 * 							array(prop_no, behalf_no, borrow_time, 是否超期, book_name)
 * 							。。。
 * 						)
 * 如果是教师则behalf里面是其帮助某个学生借的书
 */

function people_get_book_list($card_no)
{
	global $udb, $config_teacher_book_keep, $config_master_book_keep, $config_ug_book_keep;
	// 创建一个书列表
	$book_list = array();
	// 获取用户的类型
	$usertype = people_get_people_type($card_no);
	// 选取自己借的书
	$udb -> query("SELECT * FROM `ulib_borrow_record`
				LEFT JOIN `ulib_book_info` ON `ulib_book_info`.`prop_no` = `ulib_borrow_record`.`prop_no`
				WHERE `ulib_borrow_record`.`return_time` IS NULL
				AND `ulib_borrow_record`.`card_no`='{$card_no}'");
	while ($book_info = $udb -> fetch_assoc())
	{
		$out_date = false;
		// 查看是否超期
		if ($usertype == "t" && UDB::timestamp_db_to_php($book_info["borrow_time"]) +
			$config_teacher_book_keep < time())
		{
			$out_date = true;
		}
		if ($usertype == "m" && UDB::timestamp_db_to_php($book_info["borrow_time"]) +
			$config_master_book_keep < time())
		{
			$out_date = true;
		}
		if ($usertype == "u" && UDB::timestamp_db_to_php($book_info["borrow_time"]) +
			$config_ug_book_keep < time())
		{
			$out_date = true;
		}
		// 向书列表中添加数据
		$book_list[] = array("prop_no" => $book_info["prop_no"], "behalf_no" => $book_info["behalf_no"], 
							"borrow_time" => $book_info["borrow_time"], "out_date" => $out_date,
							"book_name" => $book_info["book_name"]);
	}
	$udb -> free_result();
	
	// 如果用户是教师， 还要添加帮别人借的
	if ($usertype == "t")
	{
		$out_date = false;
		$udb -> query("SELECT * FROM `ulib_borrow_record`
				LEFT JOIN `ulib_book_info` ON `ulib_book_info`.`prop_no` = `ulib_borrow_record`.`prop_no`
				WHERE `ulib_borrow_record`.`return_time` IS NULL
				AND `ulib_borrow_record`.`behalf_no`='{$card_no}'");
		while ($book_info = $udb -> fetch_assoc())
		{
			// 查看是否超期
			if ( UDB::timestamp_db_to_php($book_info["borrow_time"]) +
				$config_ug_book_keep < time())
			{
				$out_date = true;
			}
			// 向书列表中添加数据
			$book_list[] = array("prop_no" => $book_info["prop_no"], "behalf_no" => $book_info["card_no"], 
							"borrow_time" => $book_info["borrow_time"], "out_date" => $out_date,
							"book_name" => $book_info["book_name"]);
		}
		$udb -> free_result();
	}
	return $book_list;
	
}


/*
 * 检查某人是否可以借书 0代表可以， 1代表有超期书，2代表超过借书数量限制
 */
function people_can_borrow($card_no)
{
	global $config_teacher_book_count, $config_master_book_count, $config_ug_book_count;
	$list = people_get_book_list($card_no);
	// 遍历书表看是否有超期
	$has_out_date = false;
	foreach( $list as $key => $value )
	{
		if ($value["out_date"])
		{
			$has_out_date = true;
			break;
		}
	}
	if ($has_out_date)
	{
		return 1;
	}
	// 查看是否超过借书数量
	$has_out_count = false;
	$type = people_get_people_type($card_no);
	$count_book = count($list);
	if ($type == "t" && $count_book > $config_teacher_book_count)
	{
		$has_out_count = true;
	}
	if ($type == "m" && $count_book > $config_master_book_count)
	{
		$has_out_count = true;
	}
	if ($type == "u" && $count_book > $config_ug_book_count)
	{
		$has_out_count = true;
	}
	if ($has_out_count)
	{
		return 2;
	}
	return 0;
}