-- for MySQL 

DROP TABLE IF EXISTS project;
CREATE TABLE project (
       id   INT UNSIGNED NOT NULL AUTO_INCREMENT,
       name VARCHAR(128) NOT NULL,
       description VARCHAR(512) NOT NULL,
       icon_png BLOB,
       PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS pomodoro;
CREATE TABLE pomodoro (
       id   INT UNSIGNED NOT NULL AUTO_INCREMENT,
       start_time datetime NOT NULL,
       duration_minutes TINYINT UNSIGNED NOT NULL,
       completed BOOLEAN,
       PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;

DROP TABLE IF EXISTS pomodoro_log;
CREATE TABLE pomodoro_log (
       pomodoro_id   INT UNSIGNED NOT NULL,
       project_id    INT UNSIGNED NOT NULL,
       description VARCHAR(1024) NOT NULL,
       PRIMARY KEY (pomodoro_id),
       FOREIGN KEY pom_fk (pomodoro_id) REFERENCES pomodoro (id),
       FOREIGN KEY proj_fk (project_id) REFERENCES project  (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;

