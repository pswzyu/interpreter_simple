<?php
/**
 * This file will retuen JSON response
 */
 
include('./common.php');
require_once(FROOT.'lib/php/translate/class/ServicesJSON.class.php');
require_once(FROOT.'lib/php/translate/class/MicrosoftTranslator.class.php');

$translator = new MicrosoftTranslator(ACCOUNT_KEY);
$text_to_translate = $_REQUEST['text'];
$to = $_REQUEST['to'];
$from = $_REQUEST['from'];
$translator->translate($from, $to, $text_to_translate);
print_r($translator->response->jsonResponse);
print_r($translator->response);

echo "<br/><br/><br/><br/>";

echo substr($translator->response->translation, 68,-8);

