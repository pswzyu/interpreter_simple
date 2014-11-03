<?php

$string_con = '{"result":[]}
{"result":[{"alternative":[{"transcript":"you can share ideas and humping me on the robots"},{"transcript":"you can share ideas and humping me on the 405"},{"transcript":"you can share ideas and humping me on the 405 I mean"},{"transcript":"you can share ideas and humping me on the 405 I mean it"},{"transcript":"you can share ideas and humping me on the 405 how many"}],"final":true}],"result_index":0}';

$two_json = explode("}\n{", $string_con);
$first_json = $two_json[0]."}";
$second_json = "{".$two_json[1];
$best = json_decode($first_json);
$alter = json_decode($second_json);

print_r($best->result);
print_r($alter->result[0]->alternative[0]->transcript);

?>
