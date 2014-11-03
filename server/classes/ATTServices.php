<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ATTServices
 *
 * @author pswzyu
 */

require_once __DIR__.'/IServices.php';
require_once(FROOT.'lib/php/speech/lib/OAuth/OAuthTokenService.php');
require_once(FROOT.'lib/php/speech/lib/Speech/SpeechService.php');
// use any namespaced classes
use Att\Api\OAuth\OAuthTokenService;
use Att\Api\Speech\SpeechService;

class ATTServices extends IServices {
    //put your code here
    // make sure this index.php file is in the same directory as the 'lib' folder.


    // Use the app settings from developer.att.com for the following values.
    // Make sure Speech is enabled the app key/secret.
    // Enter the value from 'App Key' field
    var $clientId = '2uuw59pwuxjppqw5mv0ges92zpi1kohc';
    // Enter the value from 'Secret' field
    var $clientSecret = 's3hvwggztqtod0f0ikk5ftqstlkiap8v';
    
    var $speechSrvc;
    var $ttsSrvc;
    
    public function __construct()
    {
        // Create service for requesting an OAuth token
        $osrvc = new OAuthTokenService('https://api.att.com', $this->clientId, $this->clientSecret);
        // Get OAuth token
        $token = $osrvc->getToken('SPEECH');
        // Create service for interacting with the Speech api
        $this->speechSrvc = new SpeechService('https://api.att.com', $token);
        
        $token2 = $osrvc->getToken('TTS');
        $this->ttsSrvc = new SpeechService('https://api.att.com', $token2);
    }
    
    public function speechToText($fname)
    {
        // Translate file
        $response = $this->speechSrvc->speechToText($fname, 'Generic');
        if (!$response->getNBest())
        {
            return "";
        }
        return $response->getNBest()->getHypothesis();
    }
    public function translate($input_str, $input_lang, $output_lang)
    {
        return "";
    }
    public function textToSpeech($input_str, $fname)
    {
        $response2 = $this->ttsSrvc->textToSpeech("text/plain", $input_str);
        $f = fopen($fname,'w');
        fwrite($f, $response2);
        fclose($f);
        return "";
    }
    
}
