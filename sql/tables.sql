/*
Tirar daqui a smart tv. Por um trigger after insert.
criar trigger:
after insert on members, criar smart tv.
*/

CREATE TABLE Members (
	id 			serial 		primary key,
	email 			varchar(254) 	not null unique,
	username 		varchar(50) 	not null,

	img_path 		varchar(254) 	not null,

	reputation 		integer		not null,

	gender 			char(1) 	not null CHECK (gender IN ( 'F' , 'M' ) ),

	constraint reputationCheck CHECK(reputation >= 0)
);



/*
 
*/
CREATE TABLE News (
	id serial primary key,

	img_path varchar(254) not null,
	title varchar(90) not null,
	brief varchar(140) not null,
	content text not null,
	creation_date timestamp not null,
	event_date timestamp,
	
	show BOOLEAN not null,
	author integer not null references Members(id),
	
	likes integer not null,
	reports integer not null,
	like_weight integer not null,
	report_weight integer not null,
	constraint likesCheck CHECK(likes >= 0),
	constraint reportsCheck CHECK(reports >= 0),
	unique(title, brief, content, img_path, author)
);


/*

filter esta numa relation

owner id esta do lado do owner?
Pode dar problema ter dos dois lados.

*/
CREATE TABLE Channels (
	id serial primary key,
	name varchar(50) not null,
	filter_type varchar(50) not null,
	trending BOOLEAN not null,
	owner integer not null references Members(id)
);

/*
Mete a smart tv no members. Nao podia ser posta antes por causa de dependencias.
*/
ALTER TABLE members ADD smart_tv integer references Channels(id);

/*
last_editor may be null, since there is a common ancestral of which every tag is a child of.
to check if a certain tag is or not authenticated, we must search for it on the AuthenticatedTags table.
*/
CREATE TABLE Tags (
	id serial primary key,

	name varchar(50) not null,
	img_path varchar(254) not null,
	brief varchar(140) not null,
	authenticated boolean not null,

	last_editor integer references Members(id),
	last_edition timestamp,
	unique(name)
);

/*
Constraint, tem de ter sempre pelo menos um manager.
*/
CREATE TABLE AuthenticatedTags (
	id integer primary key references Tags(id)
);



/*################################################################################################################################*/

/*
The channel tags, which make up the filter.
*/
CREATE TABLE Channel_Tag (
	tag_id integer references Tags(id),
	channel_id integer references Channels(id),
	weight integer not null,
	constraint weightCheck CHECK(weight > 0),
	primary key (tag_id, channel_id)
);

/*
The tags hierarchy connections.
*/
CREATE TABLE Tag_Children (
	parent integer references Tags(id),
	child integer references Tags(id),
	primary key (parent, child)
);

/*
The news category.
*/
CREATE TABLE News_Category (
	news_id integer references News(id),
	tag_id integer references Tags(id),
	chosen boolean not null,
	primary key (news_id, tag_id)
);

/*
after insert on report, insert on hidden.
*/
CREATE TABLE Report (
	news_id integer references News(id),
	member_id integer references Members(id),
	weight integer not null,
	primary key (news_id, member_id),
	constraint reportWeight CHECK(weight > 0)
);

/*
After insert on likes, insert on history.
*/
CREATE TABLE Likes (
	news_id integer references News(id),
	member_id integer references Members(id),
	weight integer not null,
	primary key (news_id, member_id),
	constraint likeWeight CHECK(weight > 0)
);

/*

*/
CREATE TABLE Last_Edition (
	news_id integer references News(id),
	member_id integer references Members(id),
	edition_date timestamp not null,
	primary key (news_id, member_id)
);

/*

*/
CREATE TABLE MembersOf (
	member_id integer references Members(id),
	tag_id integer references AuthenticatedTags(id),
	is_manager BOOLEAN not null,
	primary key (member_id, tag_id) 
);

CREATE TABLE Sessions (
	member_id integer references Members(id),
	session_id text not null,
	primary key (session_id) 
);

CREATE TABLE Passwords (
	member_id integer references Members(id),
	password text not null,
	primary key (member_id) 
);

/*################################################################################################################################*/

