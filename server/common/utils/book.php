<?php
if (!defined("ULib"))
{
	die("非法访问！");
}
/*
 * 获取图书状态 返回值 n没有此书 i在馆 o借出   d删除 l丢失 r丢失已报
 */
function book_status($prop_no)
{
	global $udb;
	$is_in = true;
	// 检查图书状态是否合法
	$udb -> query("SELECT * FROM `ulib_book_info` WHERE `prop_no` = '{$prop_no}';");
	$book_info = $udb -> fetch_assoc();
	$udb -> free_result();
	// 无此书
	if (!$book_info) { return "n"; }
	if ($book_info["is_lost"] == "r") { return "r"; }
	if ($book_info["is_lost"] == "y") { return "l"; }
	if ($book_info["is_del"] == "y") { return "d"; }
	
	// 判断是否在馆
	$udb -> query("SELECT * FROM `ulib_borrow_record` WHERE `prop_no`='{$prop_no}'
		ORDER BY `borrow_time` DESC LIMIT 0,1");
	$result = $udb -> fetch_assoc();
	$udb -> free_result();
	// 没有记录或是有记录并有归还日期则是在馆
	if (!$result || ($result && $result["return_time"]))
	{
		return "i";
	}
	return "o";
}

/*
 * 获取某书借阅记录 array (
 * 							array(card_no, prop_no, behalf_no, borrow_time, return_time)
 * 							array(card_no, prop_no, behalf_no, borrow_time, return_time)
 * 							。。。
 * 						)
 */
function book_get_record($prop_no)
{
	global $udb;
	$record_list = array();
	// 按照倒序排列借阅日期
	$udb -> query("SELECT * FROM `ulib_borrow_record` WHERE `prop_no`='{$prop_no}' 
			ORDER BY `borrow_time` DESC;");
	while ($result = $udb -> fetch_assoc())
	{
		$record_list[] = array("card_no"=>$result["card_no"], "prop_no"=>$result["prop_no"],
				"behalf_no"=>$result["behalf_no"], "borrow_time"=>$result["borrow_time"],
				"return_time"=>$result["return_time"]);
	}
	return $record_list;
}

//完整内容 	id 	card_no 借书证号	prop_no 财产号	behalf_no 教师借书证号	borrow_time 借出时间	return_time 归还时间

?>