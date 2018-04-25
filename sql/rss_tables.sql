CREATE TABLE rss (
	id 			serial 		primary key,
	feed 			text 	,
	tags	 		text
);


insert into rss (feed,tags) values('http://www.nasa.gov/rss/dyn/image_of_the_day.rss','Fisica');

insert into rss (feed,tags) values('http://www.nasa.gov/rss/dyn/universe.rss','Mecânica Quântica');

insert into rss (feed,tags) values('http://www.di.fct.unl.pt/noticias/rss.xml','DI');




