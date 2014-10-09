<?php
/**
 * @Author : PSWZ-ZhangY <pswzyu@gmail.com>
 * @License : GNU GPLv3
 */
?>
<?php

/*
 * 操作数据库的类， 需要先进行实例化然后使用
 */
class UDB
{
	private $connection;
	private $query_result;
	function UDB()
	{
	}
	function connect($server, $username, $password)
	{
		$this -> connection = mysql_connect($server, $username, $password);
		return $this -> connection;
	}
	function query($sql)
	{
		$this -> query_result = mysql_query($sql, $this -> connection);
		if ($this->get_error_no() != 0)
		{
			echo "错误：：".$sql;
		}
		return $this -> query_result;
	}
        // execute a sql statement, and return if the result is empty or not
        function is_empty($sql)
        {
                $this -> query($sql);
		$tmp_result = $this -> fetch_assoc();
		$this -> free_result();
		if ($tmp_result)
                { return TRUE; }
                else
                { return FALSE; }
        }
	function fetch_assoc()
	{
                if ($this -> query_result)
                    return mysql_fetch_assoc($this -> query_result);
                else
                    return false;
	}
	function get_error_no()
	{
		return mysql_errno($this -> connection);
	}
	function free_result()
	{
		mysql_free_result($this -> query_result);
	}
	function inserted_id()
	{
		$this->query_result = mysql_query("SELECT LAST_INSERT_ID();", $this -> connection);
		$result = $this->fetch_assoc();
		$this->free_result();
		return $result["LAST_INSERT_ID()"];
	}
	function close()
	{
		mysql_close($this -> connection);
		$this -> connection = null;
		$this -> query_result = null;
		return $this -> connection;
	}
	/**
	 * 将数据库时间戳格式转换为php时间戳格式， 静态方法
	 * @param string $mysql_timestamp
	 * @return int 整形的php时间戳
	 */
	static function timestamp_db_to_php($mysql_timestamp)
	{
		return strtotime($mysql_timestamp);
	}
	/**
	 * 将数据库时间戳格式转换为php时间戳格式， 静态方法
	 * @param string $mysql_timestamp
	 * @return int 整形的php时间戳
	 */
	static function timestamp_php_to_db($php_timestamp)
	{
		return date("Y-m-d H:i:s", $php_timestamp);
	}
}



?>
