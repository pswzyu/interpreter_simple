<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// this function is for paging the year and month for display in main index page
// input format should be "2013-08", page_now starts from 0
// return the total pages and year month lis, format will still be "2013-09"
function get_yearmonth_page_i($start, $end, $page_now, $page_size)
{
    // first split the string into year and month
    $start = explode("-", $start);
    $start_y = $start[0];
    $start_m = $start[1];
    
    $end = explode("-", $end);
    $end_y = $end[0];
    $end_m = $end[1];
    
    if ( intval($start_y.$start_m) > intval( $end_y.$end_m ) ) {
        die("yearmonth.php:start is larger than end!");
    }
    
    $start_m = intval($start_m);
    $start_y = intval($start_y);
    $end_m = intval($end_m);
    $end_y = intval($end_y);
    
    $result = array( "total_page"=>0, "pagename_list"=>array() );
    
    $result["total_page"] = ceil((($end_y - $start_y)*12 + $end_m - $start_m + 1)
            / $page_size );
    
    $ignore = $page_now * $page_size;
    $have_ignored = 0;
    
    while( true )
    {
        if ($start_m < 10)
        {
            $strstart_m = "0".$start_m;
        }else{
            $strstart_m = "".$start_m;
        }
        
        if ( $have_ignored >= $ignore )
        {
            $result["pagename_list"][] = "".$start_y."-".$strstart_m;
        }
        $have_ignored ++;
        
        // increase the year month by one month
        $start_m ++;
        if ( $start_m >= 13 )
        {
            $start_m = 1;
            $start_y += 1;
        }
        
        // end when reach to the end or hit the page size requirement
        // because we should include the last month, so month should add 1
        if ( ($start_m == $end_m+1 && $start_y == $end_y)
                || count($result["pagename_list"]) >= $page_size )
        {
            break;
        }
        
    }
    
    return $result;
}

// this function is for paging the year and month for display in main index page
// input format should be "2013-08", page_now starts from 0
// return the total pages and year month lis, format will still be "2013-09"
function get_yearmonth_page_d($start, $end, $page_now, $page_size)
{
    // first split the string into year and month
    $start = explode("-", $start);
    $start_y = $start[0];
    $start_m = $start[1];
    
    $end = explode("-", $end);
    $end_y = $end[0];
    $end_m = $end[1];
    
    if ( intval($start_y.$start_m) > intval( $end_y.$end_m ) ) {
        die("yearmonth.php:start is larger than end!");
    }
    
    $start_m = intval($start_m);
    $start_y = intval($start_y);
    $end_m = intval($end_m);
    $end_y = intval($end_y);
    
    $result = array( "total_page"=>0, "pagename_list"=>array() );
    
    $result["total_page"] = ceil((($end_y - $start_y)*12 + $end_m - $start_m + 1)
            / $page_size );
    
    $ignore = $page_now * $page_size;
    $have_ignored = 0;
    
    while( true )
    {
        if ($end_m < 10)
        {
            $strend_m = "0".$end_m;
        }else{
            $strend_m = "".$end_m;
        }
        
        if ( $have_ignored >= $ignore )
        {
            $result["pagename_list"][] = "".$end_y."-".$strend_m;
        }
        $have_ignored ++;
        
        // increase the year month by one month
        $end_m --;
        if ( $end_m < 1 )
        {
            $end_m = 12;
            $end_y -= 1;
        }
        
        // end when reach to the end or hit the page size requirement
        // because we should include the last month, so month should add 1
        if ( ($start_m == $end_m+1 && $start_y == $end_y)
                || count($result["pagename_list"]) >= $page_size )
        {
            break;
        }
        
    }
    
    return $result;
}

?>