<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$enum_consulate = array( "", "BeiJing", "ChengDu", "Chennai", "Europe",
        "GuangZhou", "HongKong", "Kolkata", "MexicoCity", "Montreal",
        "Mumbai", "NewDelhi", "Ottawa", "Quebec", "ShangHai", "ShenYang",
        "Tijuana", "Toronto", "Vancouver", "Others" );

$enum_visatype = array( "", "F1", "F2", "H1", "H4", "J1", "J2", "B1", "B2", "L1", "L2" );

$enum_visaentry = array( "", "New", "Renewal" );

$enum_status = array( "", "Clear", "Pending", "Reject" );

$enum_degree = array( "", "N/A", "BS", "MS", "Ph.D", "Others" );

class Enums
{
    public static $enum_consulate = array( "", "BeiJing", "ChengDu", "Chennai", "Europe",
        "GuangZhou", "HongKong", "Kolkata", "MexicoCity", "Montreal",
        "Mumbai", "NewDelhi", "Ottawa", "Quebec", "ShangHai", "ShenYang",
        "Tijuana", "Toronto", "Vancouver", "Others" );

    public static $enum_visatype = array( "", "F1", "F2", "H1", "H4", "J1", "J2", "B1", "B2", "L1", "L2" );

    public static $enum_visaentry = array( "", "New", "Renewal" );

    public static $enum_status = array( "", "Clear", "Pending", "Reject" );

    public static $enum_degree = array( "", "N/A", "BS", "MS", "Ph.D", "Others" );
}

?>