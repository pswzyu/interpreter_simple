<?php 

$config_database_server = "";
$config_database_username = "";
$config_database_password = "";

$uploaddir = '/var/www/interpreter/uploads/';
$xunfei_bin_dir = '/cloudhome/nosys/interpreter_simple/server/tools/xunfei/bin/';
$translator_cache_dir = '/cloudhome/nosys/interpreter_simple/server/lib/php/translate/cache/';


/**
 * @var string Microsoft/Bing Primary Account Key
 */
if (!defined('ACCOUNT_KEY')) {
    define('ACCOUNT_KEY', 'rMJyeee877Gspc+jGhec/s/eOeLomlwUQe2Xsivt5nI');
}
if (!defined('CACHE_DIRECTORY')) {
    define('CACHE_DIRECTORY', $translator_cache_dir);
}
if (!defined('LANG_CACHE_FILE')) {
    define('LANG_CACHE_FILE', 'lang.cache');
}
if (!defined('ENABLE_CACHE')) {
    define('ENABLE_CACHE', true);
}
if (!defined('UNEXPECTED_ERROR')) {
    define('UNEXPECTED_ERROR', 'There is some un expected error . please check the code');
}
if (!defined('MISSING_ERROR')) {
    define('MISSING_ERROR', 'Missing Required Parameters ( Language or Text) in Request');
}

?>
