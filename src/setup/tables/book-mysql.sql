create table book (
  id int auto_increment primary key not null,
  title varchar(255) unique not null, 
  binding enum ('paper','cloth') not null,
  quantity int not null default 0
)
