DROP FUNCTION get_age(int);
DROP FUNCTION get_age_ratio(int, double precision);
DROP FUNCTION get_likes_rank(int, int);
DROP FUNCTION get_regular_rank(int, int);
DROP FUNCTION get_smart_rank(int, int);
DROP FUNCTION get_news_rank(int, int);
DROP FUNCTION calc_rank(int, int);



CREATE OR REPLACE FUNCTION get_age( news_id int ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
  	creat_date timestamp;
  	news_age interval;
  	num_seconds double precision;
BEGIN
	creat_date := (select creation_date from news where id = news_id);
	news_age := age(current_timestamp, creat_date);
  	num_seconds := (SELECT EXTRACT(EPOCH FROM news_age));
  	RETURN num_seconds;
END
$$;


CREATE OR REPLACE FUNCTION get_age_ratio( channel_id int , news_age double precision ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
  	hour double precision;
  	minute double precision;
  	trending_constant double precision;
  	regular_constant double precision;
  	rank double precision;
	is_trending boolean;
BEGIN
	minute := 60.0;
	hour := 60.0 * minute;
	trending_constant := minute;
	regular_constant := 12.0 * hour;
	is_trending := (select trending from channels where id = channel_id);
	if is_trending then
		rank := (news_age + trending_constant) ^ 2.0;
	else
		rank := (news_age + regular_constant);
	end if;
	RETURN rank;
END
$$;




CREATE OR REPLACE FUNCTION get_likes_rank( _channel_id int , _news_id int ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
	num_likes double precision;
	num_reports double precision;
	
  	g double precision;
  	affinity double precision;
  	
BEGIN
	SELECT likes, reports 
		INTO num_likes, num_reports 
		FROM news 
		WHERE id = _news_id;
		
	g := 1.0 + num_likes - num_reports;
	
	--gets the number of hits
	SELECT count(*) into affinity
	FROM (
		(SELECT tag_id FROM news_category WHERE news_id = _news_id)
		INTERSECT
		(SELECT tag_id FROM channel_tag WHERE channel_id = _channel_id)
	) as tags;
	
	affinity = affinity ^ 0.5;
	RETURN affinity * g;
END
$$;

CREATE OR REPLACE FUNCTION get_regular_rank( _channel_id int , _news_id int ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
	num_likes double precision;
	num_reports double precision;
	likes_weight double precision;
	reports_weight double precision;
	
  	g double precision;
  	h double precision;
  	affinity double precision;
  	
BEGIN
	SELECT likes, reports, like_weight, report_weight 
		INTO num_likes, num_reports, likes_weight, reports_weight 
		FROM news 
		WHERE id = _news_id;
		
	g := 1.0 + num_likes - num_reports;
	h := (likes_weight) * 100.0 / ( likes_weight + reports_weight );
	
	--gets the number of hits
	SELECT COALESCE(sum(tag_weight),0) INTO affinity
	FROM (
		SELECT COALESCE(weight,0) AS tag_weight --for each tag that is on the news and on the channel.
		FROM news_category INNER JOIN channel_tag ON (news_category.tag_id = channel_tag.tag_id)
		WHERE news_id = _news_id AND channel_id = _channel_id
	) as weights;
	RETURN affinity * g * h;
END
$$;

CREATE OR REPLACE FUNCTION get_smart_rank( _channel_id int , _news_id int ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
	num_likes double precision;
	num_reports double precision;
	likes_weight double precision;
	reports_weight double precision;
	
  	g double precision;
	tmp numeric(10);
  	h double precision;
  	affinity double precision;
  	
BEGIN
	SELECT likes, reports, like_weight, report_weight 
		INTO num_likes, num_reports, likes_weight, reports_weight 
		FROM news 
		WHERE id = _news_id;
		
	tmp := 1 + num_likes - num_reports;
	g := log(10.0,tmp);
	h := (likes_weight) * 100.0 / ( likes_weight + reports_weight );
	
	--gets the affinity
	SELECT log(2.718,COALESCE(sum(affinity_weight),2.0)) INTO affinity
	FROM (
			SELECT 2.0 + COALESCE(weight,0.0) AS affinity_weight
			FROM (
				news_category 
				LEFT JOIN 
				channel_tag 
				ON (news_category.tag_id = channel_tag.tag_id)
			)
			WHERE news_id = _news_id AND channel_id = _channel_id
	) as weights;

	RETURN affinity * g * h;
END
$$;

CREATE OR REPLACE FUNCTION get_news_rank( channel_id int , news_id int ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
	channel_type text;
  	rank double precision;
BEGIN
	channel_type := (select filter_type from channels where id = channel_id);
	if channel_type = 'smart' then
		rank := get_smart_rank(channel_id, news_id);
	elsif channel_type = 'likes' then
		rank := get_likes_rank(channel_id, news_id);
	else
		rank := get_regular_rank(channel_id, news_id);
	end if;
	RETURN rank;
END
$$;




CREATE OR REPLACE FUNCTION calc_rank( channel_id int, news_id int ) RETURNS double precision LANGUAGE plpgsql AS $$
DECLARE
  news_age double precision;
  news_age_ratio double precision;
  news_rank double precision;
  affinity double precision;
  total_rank double precision;
BEGIN
	news_age := get_age(news_id);
	news_age_ratio := get_age_ratio(channel_id, news_age);
	news_rank := get_news_rank(channel_id, news_id);
  	RETURN news_rank / news_age_ratio;
END
$$;
