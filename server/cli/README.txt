first, create a group, then this group will be used for containing all users
then, add users with their photos
then, train the model for the group, every time that add new user, need to re-train

use the real_name in user_info table as the <username> in the commands below
for group_name, use interpreter_group

0. create a group
php create_group.php <group_name>

1. register a new user
php register_person.php <username> <group_name> <pic1> <pic2> ...

2. train model of the group
php train_group_model.php <group_name>

3. testing
php test_image.php <group_name> <img_filename>
