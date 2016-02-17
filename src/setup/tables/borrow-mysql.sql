create table borrow (
  id integer auto_increment primary key not null,
  book_id integer not null,
  user_id integer not null,
  borrowed_at date,
  foreign key(book_id) references book(id),
  foreign key(user_id) references user(id),
  unique(book_id,user_id)
)
