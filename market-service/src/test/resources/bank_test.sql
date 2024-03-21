
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

INSERT INTO `permission` (`permission_id`, `description`, `name`) VALUES
(2, 'can_manage_users', 'can_manage_users');

INSERT INTO `user` (`user_id`, `active`, `email`, `first_name`, `jmbg`, `last_name`, `password`, `phone_number`, `position`) VALUES
    (100, b'1', 'admin@admin.com', 'admin', 'admin', 'admin', '$2a$10$PBWT9wzA7OPpZPr5lVNxj.SLlHhrBrUzHH/wOG6sqfOp3wbYk8Kze', '1234567890', 'admin');

INSERT INTO `user_permissions` (`user_id`, `permission_id`) VALUES
    (100, 2);

COMMIT;