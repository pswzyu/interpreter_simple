<?php
/**
 * @Author : PSWZ-ZhangY <pswzyu@gmail.com>
 * @License : GNU GPLv3
 */

	define("WEBROOT", "/chisha/"); // 定义地址根目录（浏览器看到的根目录）， 用于网页中的链接定位
	define("FILEROOT", "/var/www/chisha/"); // 定义文件系统中的网站根目录， 用于引用文件和附件保存
	define("HTTPROOT", "http://172.17.178.10/chisha/"); // 定义地址根目录（浏览器看到的根目录）， 用于网页中的链接定位
	
	if (!defined("SECURE")) // 如果不是通过定义了SECURE的文件进行的访问（即用户意外访问）
	{
		require(FILEROOT."template/html/illegal_visit.php"); // 显示出错信息
		die(); // 终止程序执行
	}

	require_once(FILEROOT."config/global.php"); // 引入全局配置信息
	/**
	 * 与数据库相关的函数
	 *
	 */
	if ($config_database_type == "mysql") // 引入有关数据库的核心类
	{
		require_once(FILEROOT."config/database/mysql.php"); // 引入连接配置文件
		require_once(FILEROOT."lib/db/mysql.inc.php"); // 引入数据库核心类
	}

	$db = new DB(); // 创建数据库工具类对象
	// 连接数据库
	$db -> connect($config_database_server, $config_database_username, $config_database_password);	
	$db -> query("USE `chisha`"); // 切换到chisha数据库
	$db -> query("SET NAMES UTF8"); // 设定传输编码
	/**
	 * 与用户IP相关的函数
	 *
	 */
	//获得用户的IP地址, 引用了156544632@163.com 在FYblog中的代码
	if(getenv('HTTP_CLIENT_IP') && strcasecmp(getenv('HTTP_CLIENT_IP'), 'unknown')) {
		$chisha_useripaddress = getenv('HTTP_CLIENT_IP');
	} elseif(getenv('HTTP_X_FORWARDED_FOR') && strcasecmp(getenv('HTTP_X_FORWARDED_FOR'), 'unknown')) {
		$chisha_useripaddress = getenv('HTTP_X_FORWARDED_FOR');
	} elseif(getenv('REMOTE_ADDR') && strcasecmp(getenv('REMOTE_ADDR'), 'unknown')) {
		$chisha_useripaddress = getenv('REMOTE_ADDR');
	} elseif(isset($_SERVER['REMOTE_ADDR']) && $_SERVER['REMOTE_ADDR'] && strcasecmp($_SERVER['REMOTE_ADDR'], 'unknown')) {
		$chisha_useripaddress = $_SERVER['REMOTE_ADDR'];
	}
	
	// 获取当前时间戳， 非需要确切的当前时间则使用此处时间
	$chisha_timestamp = time();
	$chisha_timestamp_micro =  microtime(true); // 是含微妙的timestamp	
	
	$chisha_username = ""; // 用户名， 如果调用了后边的checkLoginStatus验证用户身份通过则会覆盖这里
	$chisha_userid = 0; // 用户id， 如果后边的checkLoginStatus验证用户身份通过则会覆盖这里
	$chisha_duty_in_website = "g"; // 用户站内职务， 如果调用了后边的checkLoginStatus验证用户身份通过则会覆盖这里, g 表示访客
	
	
	/*
	 * 获取用户站内职务， 私有方法， 不要调用
	 * @param string $username 用户名
	 * @return string $duty 职务代码“n”“a”
	 */
	function chisha_private_get_duty_in_website()
	{
		global $chisha_username, $db;
		if ($chisha_username)
		{
			$db -> query("SELECT `duty_in_website` FROM `chisha`.`chisha_user`
						WHERE `username` ='".$chisha_username."';");
			$user_info = $db -> fetch_assoc();
			$db -> free_result();
			return $user_info["duty_in_website"];
		}
		return "g";
	}
	/**
	 * 用于检查用户登录状态的函数， 
	 * 使用cookie， 并对其进行解密， 
	 * 验证用户身份， 判断是否已经登录。
	 * 
	 * @return string username 用户名， 为空则表示当前用户验证失败
	 */
	function chisha_check_login_status()
	{
		global $chisha_username, $chisha_userid, $chisha_timestamp, $db;
		// 从数据库中获得session信息
		$cookie_username = @$_COOKIE["chisha_username"]; // 将COOKIE中的值放进变量中
		$cookie_authcode = @$_COOKIE["chisha_authcode"]; // 方便操作
		if ($cookie_username && $cookie_authcode)
		{
			$db -> query("SELECT `username`, `cookie_encrypted`, `set_time`, `valid_time`
						FROM `chisha`.`chisha_session`
						WHERE `username` = '{$cookie_username}';");
			$fetch = $db -> fetch_assoc(); // 将查询结果保存
			$db -> free_result();
			if ($fetch
				&& $fetch["cookie_encrypted"] == $cookie_authcode
				&& (DB::timestamp_db_to_php($fetch["set_time"])
					+ $fetch["valid_time"]) > $chisha_timestamp)
				// 如果session中有这个用户名
				// 加密码一样
				// cookie的有效时间没有过期
			{
				$chisha_username = $cookie_username; // 将当前用户名置为cookie中的用户名
				$db->query("SELECT `id` FROM `chisha_user` WHERE `username`='{$chisha_username}'");
				$fetch = $db->fetch_assoc();
				//print_r($fetch);
				$db->free_result();
				$chisha_userid = $fetch["id"];
				// 刷新cookie中的authcode和数据库中的时间等, true 表示为刷新
				chisha_private_refresh_cookie_login($chisha_username, true);
				return true; // 返回表示用户已经登录
			}else
			{
				chisha_logout($cookie_username);
			}
		}
		return false;
	}
	/**
	 * 用于刷新cookie中的authcode和数据库中的session信息, 私有方法， 不要调用
	 * @TODO : 修改cookie有效时间
	 * @param string username 用户名, boolean refresh 是否是刷新（false为新建记录）
	 */
	function chisha_private_refresh_cookie_login($username, $refresh)
	{
		global $chisha_timestamp, $config_cookie_valid_time, $db;

		$authcode = chisha_private_get_cookie_encrypted($username, $chisha_timestamp, $config_cookie_valid_time); // 获取加密后的文本
		if ($refresh)
		{
			$db -> query("UPDATE `chisha`.`chisha_session`
				SET `set_time` = '".DB::timestamp_php_to_db($chisha_timestamp)."',
					`cookie_encrypted` = '{$authcode}',
					`valid_time` = {$config_cookie_valid_time}
				WHERE `username` = '{$username}';"); // 更新session内容
			setcookie("chisha_username", $username, $chisha_timestamp + $config_cookie_valid_time, WEBROOT); // 插入用户名
			setcookie("chisha_authcode", $authcode,
				$chisha_timestamp + $config_cookie_valid_time, WEBROOT); // 更新cookie中的authcode, WEBROOT
		}else
		{
			$db -> query("INSERT INTO `chisha`.`chisha_session`
				(
					`id`, `username`, `cookie_encrypted`, `set_time`, `valid_time`
				)VALUES
				(
					NULL, '{$username}', '{$authcode}',
					'".DB::timestamp_php_to_db($chisha_timestamp)."','{$config_cookie_valid_time}'
				);"); // 插入session内容
			
			setcookie("chisha_username", $username, $chisha_timestamp + $config_cookie_valid_time, WEBROOT); // 插入用户名
			setcookie("chisha_authcode", $authcode,
				$chisha_timestamp + $config_cookie_valid_time, WEBROOT); // 插入authcode
		}
	}
	/**
	 * 用于用户登录的函数， 
	 * 使用cookie，
	 * 更新用户登录状态
	 * 
	 * @param string username 用户名, string password 密码
	 * @return boolean success 是否成功
	 */
	 function chisha_login($username, $password)
	 {
	 	global $db; // 使用外部变量
	 	$db -> query("SELECT `username` FROM `chisha`.`chisha_session`
	 			WHERE `username` = '{$username}';"); // 查看session表中是否已经有此用户名
	 	$islogin = $db -> fetch_assoc();
	 	$db -> free_result();
	 	if ($islogin) // 如果有说明用户已经登录
	 	{
	 		chisha_private_refresh_cookie_login($username, true);
	 		return true;
	 	}else
	 	{
			$db -> query("SELECT `password_en`
						FROM `chisha`.`chisha_user`
						WHERE `username` = '{$username}';"); // 获取用户的加密密码
			$user_info = $db -> fetch_assoc(); // 将其放入变量
			$db -> free_result();
			if (chisha_get_password_encrypted($password) == $user_info["password_en"]) // 如果和传入的相同
			{
				chisha_private_refresh_cookie_login($username, false); // 创建新的session记录， 并刷新用户cookie
				$chisha_duty_in_website = chisha_private_get_duty_in_website();
				return true; // 返回true表示登录成功
			}else
			{
				chisha_logout($username); // 如果有session记录则删除， 并删除用户cookie
				return false; // 返回false 表示登录失败
			}
		}
	 }
	 /**
	 * 用于用户注销的函数， 
	 * 使用cookie， 并对其进行解密， 
	 * 更新用户登录状态， 进行注销
	 * 
	 * @return string username 用户名
	 */
	 function chisha_logout($username)
	 {
	 	if ($username)
	 	{
	 		global $db; // 使用外部变量
		 	global $config_cookie_valid_time;
			$query_result = $db -> query("DELETE FROM `chisha`.`chisha_session`
						WHERE `username` = '{$username}';");
			setcookie("chisha_username", "", 0); // 删除用户名
			setcookie("chisha_authcode", "", 0); // 删除authcode
			if ($query_result == 1)
			{
				return true;
			}
			return false;
			
		}
		return false;
	 }
	 /**
	 * 用于获取cookie_encrypted的函数， 私有方法， 不要调用
	 *  
	 * @param string $username 用户名, int $set_time cookie设定的时间
	 * 		int $valid_time 有效时长
	 * @return string cookie_encrypted
	 */
	 function chisha_private_get_cookie_encrypted($username, $set_time, $valid_time)
	 {
	 	global $session_encrypt_code; // 获得cookie“短语密码”
	 	return md5("encoded".$username.$session_encrypt_code.$set_time.$valid_time); // 返回加密后的密文
	 	//return "encode : " .$username."-----".$set_time."---------".$valid_time;
	 }
	 /**
	  * 用于获取用户密码的加密版本的函数， 外部调用仅用在注册时
	  * @param string $password 明码
	  * @return string $encrpted_password 加密的密码
	  */
	 function chisha_get_password_encrypted($password)
	 {
	 	return md5($password);
	 }

?>