
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
(1, 'test_position', 'test');

INSERT INTO `user` (`user_id`, `active`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`, `position`) VALUES
    (1, b'1', 'admin@admin.com', 'admin', 'admin', 'admin', 'admin', '1234567890', 'admin');

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (1, 1);

COMMIT;