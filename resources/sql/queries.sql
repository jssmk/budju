-- :name new-user! :insert :1
-- :doc new user link value
INSERT INTO userlink VALUES ( );
-- :name new-user-data! :! :n
-- :doc new user data record
INSERT INTO user
(
userid,     -- uuid
username,   -- varchar 50
email,      -- varchar 50
admin,      -- boolean
last_login, -- time
password   -- varchar 300
)
VALUES ( :userid, :username, :email, :admin, NULL, :password );

-- :name del-user! :! :n
-- :doc deletes user data
DELETE FROM user WHERE userid = :uuid;


-- :name new-plan! :insert :1
-- :doc connects a new plan to a user
INSERT INTO planlink ( userid ) VALUES ( :userid );
-- :name new-plan-data! :! :n
-- :doc inserts plan/initiative data
INSERT INTO plan
(
planid, -- UUID
title, -- varhchar 200 
status, -- integer
description -- varchar 2000
)
VALUES (:planid, :title, 0, :description );


-- :name new-comment! :insert :1
-- :doc connects a new comment to a user and a plan
INSERT INTO commentlink ( planid, userid ) values ( :planid, :userid )
-- :name new-comment-data! :! :n
-- :doc inserts comment data
INSERT INTO comment
(
commid, -- int
content -- varchar 1000
)
VALUES ( :commid, :content );


-- :name new-vote! :insert :1
-- :doc connects a new vote to a user and a plan
INSERT INTO votelink ( planid, userid ) values ( :planid, :userid )
-- :name new-vote-data! :! :n
-- :doc inserts vote data
INSERT INTO vote
(
voteid, -- int
type -- varchar 10
)
VALUES ( :voteid, :type );


-- :name get-plan-details :? :1
-- :doc describes plan details
select plan.title, plan.status, plan.description, plan.created from plan
where planid = :planid;


-- :name get-plan-comments :? :*
-- :doc all comments in a plan
select user.userid, comment.content, comment.created from plan
left join commentlink on plan.planid = commentlink.planid
inner join comment on comment.commid = commentlink.commid
inner join user on user.userid = commentlink.userid
where plan.planid = :planid;


-- :name get-plan-votes :? :*
-- :doc all votes in a plan
select user.userid, vote.type, vote.created from plan
left join votelink on plan.planid = votelink.planid
inner join vote on vote.voteid = votelink.voteid
inner join user on user.userid = votelink.userid
where plan.planid = :planid;
