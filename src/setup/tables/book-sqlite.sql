create table book (
  id integer primary key not null,
  title text unique not null collate nocase,
  binding text not null collate nocase,
  quantity integer not null default 0
)

