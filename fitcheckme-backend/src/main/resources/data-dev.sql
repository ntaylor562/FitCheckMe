--Sample data

--Users (# of users: 5)
insert into community.user (username, email, password, bio) values ('bender', 'bfreyne0@boston.com', '$2a$04$PQRD.iXQfRdxjwyBMPoTLuYMCFQnD8e3eXWAPkiOLp/TU1a9IDH0G', 'bender profile bio');
insert into community.user (username, email, password, bio) values ('ependell0', 'bissit1@redcross.org', '$2a$04$egyPHdKJeDTlrMgzHPE0SeDtCCStd3KuFfZ4/RlqtHc3mtU6pS2s6', 'consequat dui nec nisi volutpat eleifend donec ut dolor morbi vel lectus in quam fringilla rhoncus mauris enim');
insert into community.user (username, email, password, bio) values ('tpisco1', 'elowdes2@ox.ac.uk', '$2a$04$gH6OMtd2KZlgJcb9q28X7uLskmwGJEM17NA6/qfoUmUPl370elwm.', 'vestibulum sed magna at nunc commodo placerat praesent blandit nam nulla integer pede justo lacinia eget tincidunt eget tempus');
insert into community.user (username, email, password, bio) values ('cstelfox2', 'lthorntondewhirst3@icio.us', '$2a$04$Ey51lphteIUxX0ngTBWbQ.9ayT6VySj8EuuX9kxby/gZv6A1d6iFC', 'venenatis turpis enim blandit mi in porttitor pede justo eu massa donec dapibus duis at velit eu est congue elementum');
insert into community.user (username, email, password, bio) values ('sdredge3', 'mfarans4@microsoft.com', '$2a$04$q2RupVgrk5MHzjmVtvUUQuQtQJav4DpQLLoZ5W7iqF47QVZSMOLDq', null);

--Followings (# of followings: 5)
insert into community.following (follower_id, followee_id, follow_date) values (1, 2, '2023-06-17T08:08:32Z');
insert into community.following (follower_id, followee_id, follow_date) values (2, 9, '2021-12-24T23:20:49Z');
insert into community.following (follower_id, followee_id, follow_date) values (2, 3, '2023-05-18T02:25:41Z');
insert into community.following (follower_id, followee_id, follow_date) values (3, 7, '2022-04-29T13:36:15Z');
insert into community.following (follower_id, followee_id, follow_date) values (2, 5, '2021-12-18T01:39:23Z');

--Tags (# of tags: 4)
insert into app.tag (tag_name) values ('green');
insert into app.tag (tag_name) values ('summer');
insert into app.tag (tag_name) values ('blue');
insert into app.tag (tag_name) values ('weekend');

--Outfits (# of outfits: 4)
insert into app.outfit (user_id, outfit_name, outfit_desc, outfit_creation_date) values (3, 'consequat ut', 'et ultrices posuere cubilia curae duis faucibus accumsan odio curabitur convallis duis consequat dui nec nisi volutpat eleifend donec', '2023-05-12T07:05:07Z');
insert into app.outfit (user_id, outfit_name, outfit_desc, outfit_creation_date) values (1, 'eleifend luctus ultricies', 'donec ut dolor morbi vel lectus in quam fringilla rhoncus mauris enim leo rhoncus sed vestibulum', '2022-08-20T05:25:30Z');
insert into app.outfit (user_id, outfit_name, outfit_desc, outfit_creation_date) values (1, 'consequat metus', 'et magnis dis parturient montes nascetur ridiculus mus vivamus vestibulum sagittis sapien cum sociis natoque penatibus', '2023-04-10T06:51:36Z');
insert into app.outfit (user_id, outfit_name, outfit_desc, outfit_creation_date) values (4, 'vitae quam suspen', 'libero rutrum ac lobortis vel dapibus at diam nam tristique', '2023-04-15T13:52:50Z');

--Outfit tags (# of tags: 6)
insert into app.outfit_tag (outfit_id, tag_id) values (2, 3);
insert into app.outfit_tag (outfit_id, tag_id) values (1, 1);
insert into app.outfit_tag (outfit_id, tag_id) values (4, 3);
insert into app.outfit_tag (outfit_id, tag_id) values (3, 2);
insert into app.outfit_tag (outfit_id, tag_id) values (1, 3);
insert into app.outfit_tag (outfit_id, tag_id) values (1, 4);

--Garments (# of garments: 10)
insert into app.garment (user_id, garment_name) values (1, 'nisi at');
insert into app.garment (user_id, garment_name) values (2, 'odio in');
insert into app.garment (user_id, garment_name) values (3, 'condimentum');
insert into app.garment (user_id, garment_name) values (2, 'ultrices');
insert into app.garment (user_id, garment_name) values (2, 'donec vitae');
insert into app.garment (user_id, garment_name) values (2, 'aliquam augue quam');
insert into app.garment (user_id, garment_name) values (5, 'auctor sed');
insert into app.garment (user_id, garment_name) values (1, 'purus aliquet at');
insert into app.garment (user_id, garment_name) values (3, 'ligula vehicula consequat');
insert into app.garment (user_id, garment_name) values (1, 'sed vel');

--Garment tags (# of tags: 20)
insert into app.garment_tag (garment_id, tag_id) values (2, 2);
insert into app.garment_tag (garment_id, tag_id) values (5, 2);
insert into app.garment_tag (garment_id, tag_id) values (6, 2);
insert into app.garment_tag (garment_id, tag_id) values (1, 4);
insert into app.garment_tag (garment_id, tag_id) values (4, 3);
insert into app.garment_tag (garment_id, tag_id) values (2, 3);
insert into app.garment_tag (garment_id, tag_id) values (7, 1);
insert into app.garment_tag (garment_id, tag_id) values (6, 3);
insert into app.garment_tag (garment_id, tag_id) values (5, 4);
insert into app.garment_tag (garment_id, tag_id) values (2, 1);
insert into app.garment_tag (garment_id, tag_id) values (2, 4);
insert into app.garment_tag (garment_id, tag_id) values (10, 1);
insert into app.garment_tag (garment_id, tag_id) values (9, 2);
insert into app.garment_tag (garment_id, tag_id) values (3, 1);
insert into app.garment_tag (garment_id, tag_id) values (1, 3);
insert into app.garment_tag (garment_id, tag_id) values (3, 3);
insert into app.garment_tag (garment_id, tag_id) values (8, 4);
insert into app.garment_tag (garment_id, tag_id) values (3, 4);
insert into app.garment_tag (garment_id, tag_id) values (5, 3);
insert into app.garment_tag (garment_id, tag_id) values (9, 4);

--Garment urls (# of urls: 10)
insert into app.garment_url (garment_id, garment_url) values (5, 'http://dell.com/nunc/nisl/duis.html?nibh=purus&quisque=phasellus&id=in&justo=felis&sit=donec&amet=semper&sapien=sapien&dignissim=a&vestibulum=libero&vestibulum=nam&ante=dui&ipsum=proin&primis=leo&in=odio&faucibus=porttitor&orci=id&luctus=consequat&et=in&ultrices=consequat&posuere=ut&cubilia=nulla&curae=sed&nulla=accumsan&dapibus=felis&dolor=ut&vel=at&est=dolor&donec=quis&odio=odio&justo=consequat');
insert into app.garment_url (garment_id, garment_url) values (2, 'https://indiegogo.com/praesent/blandit.aspx?in=pede&hac=lobortis&habitasse=ligula&platea=sit&dictumst=amet&morbi=eleifend&vestibulum=pede&velit=libero&id=quis&pretium=orci&iaculis=nullam&diam=molestie&erat=nibh&fermentum=in&justo=lectus&nec=pellentesque&condimentum=at&neque=nulla&sapien=suspendisse&placerat=potenti&ante=cras&nulla=in&justo=purus&aliquam=eu&quis=magna&turpis=vulputate&eget=luctus&elit=cum&sodales=sociis&scelerisque=natoque&mauris=penatibus&sit=et&amet=magnis&eros=dis&suspendisse=parturient&accumsan=montes&tortor=nascetur&quis=ridiculus&turpis=mus&sed=vivamus&ante=vestibulum&vivamus=sagittis&tortor=sapien&duis=cum&mattis=sociis&egestas=natoque&metus=penatibus&aenean=et&fermentum=magnis&donec=dis&ut=parturient&mauris=montes&eget=nascetur&massa=ridiculus&tempor=mus&convallis=etiam&nulla=vel&neque=augue&libero=vestibulum&convallis=rutrum&eget=rutrum&eleifend=neque&luctus=aenean&ultricies=auctor&eu=gravida&nibh=sem');
insert into app.garment_url (garment_id, garment_url) values (4, 'http://elpais.com/nulla/ut.json?ultrices=ipsum&vel=praesent&augue=blandit&vestibulum=lacinia');
insert into app.garment_url (garment_id, garment_url) values (8, 'http://geocities.com/ullamcorper/purus/sit.jpg?at=venenatis&velit=tristique&vivamus=fusce&vel=congue&nulla=diam&eget=id&eros=ornare&elementum=imperdiet&pellentesque=sapien&quisque=urna&porta=pretium&volutpat=nisl&erat=ut&quisque=volutpat&erat=sapien&eros=arcu&viverra=sed&eget=augue&congue=aliquam&eget=erat&semper=volutpat&rutrum=in&nulla=congue');
insert into app.garment_url (garment_id, garment_url) values (10, 'http://usatoday.com/ac/consequat/metus/sapien.html?fusce=pharetra&lacus=magna&purus=vestibulum&aliquet=aliquet&at=ultrices&feugiat=erat&non=tortor&pretium=sollicitudin&quis=mi&lectus=sit&suspendisse=amet&potenti=lobortis&in=sapien&eleifend=sapien&quam=non&a=mi&odio=integer&in=ac&hac=neque&habitasse=duis&platea=bibendum&dictumst=morbi&maecenas=non&ut=quam&massa=nec&quis=dui&augue=luctus&luctus=rutrum&tincidunt=nulla&nulla=tellus&mollis=in&molestie=sagittis&lorem=dui&quisque=vel&ut=nisl&erat=duis&curabitur=ac&gravida=nibh&nisi=fusce&at=lacus&nibh=purus&in=aliquet&hac=at&habitasse=feugiat&platea=non&dictumst=pretium&aliquam=quis&augue=lectus&quam=suspendisse&sollicitudin=potenti&vitae=in&consectetuer=eleifend&eget=quam&rutrum=a&at=odio&lorem=in');
insert into app.garment_url (garment_id, garment_url) values (7, 'http://businessweek.com/primis/in/faucibus/orci/luctus/et/ultrices.jpg?dui=tincidunt&maecenas=eget');
insert into app.garment_url (garment_id, garment_url) values (1, 'https://moonfruit.com/ante.html?tincidunt=convallis&nulla=morbi&mollis=odio&molestie=odio&lorem=elementum&quisque=eu&ut=interdum&erat=eu&curabitur=tincidunt&gravida=in&nisi=leo&at=maecenas&nibh=pulvinar&in=lobortis&hac=est&habitasse=phasellus&platea=sit&dictumst=amet&aliquam=erat&augue=nulla&quam=tempus&sollicitudin=vivamus&vitae=in&consectetuer=felis&eget=eu&rutrum=sapien&at=cursus&lorem=vestibulum&integer=proin&tincidunt=eu&ante=mi&vel=nulla&ipsum=ac&praesent=enim&blandit=in&lacinia=tempor&erat=turpis&vestibulum=nec&sed=euismod&magna=scelerisque&at=quam&nunc=turpis&commodo=adipiscing&placerat=lorem&praesent=vitae&blandit=mattis&nam=nibh&nulla=ligula&integer=nec&pede=sem&justo=duis&lacinia=aliquam&eget=convallis&tincidunt=nunc&eget=proin&tempus=at&vel=turpis&pede=a&morbi=pede&porttitor=posuere&lorem=nonummy&id=integer&ligula=non&suspendisse=velit&ornare=donec&consequat=diam&lectus=neque&in=vestibulum&est=eget&risus=vulputate');
insert into app.garment_url (garment_id, garment_url) values (3, 'http://newyorker.com/erat/curabitur/gravida.js?quisque=odio&ut=condimentum&erat=id&curabitur=luctus&gravida=nec&nisi=molestie&at=sed&nibh=justo&in=pellentesque&hac=viverra&habitasse=pede&platea=ac&dictumst=diam&aliquam=cras&augue=pellentesque&quam=volutpat&sollicitudin=dui&vitae=maecenas&consectetuer=tristique&eget=est&rutrum=et&at=tempus&lorem=semper&integer=est&tincidunt=quam&ante=pharetra&vel=magna&ipsum=ac&praesent=consequat&blandit=metus&lacinia=sapien');
insert into app.garment_url (garment_id, garment_url) values (8, 'http://washingtonpost.com/lorem/ipsum/dolor/sit.jpg?sed=dolor&tincidunt=quis&eu=odio&felis=consequat&fusce=varius&posuere=integer&felis=ac&sed=leo&lacus=pellentesque&morbi=ultrices&sem=mattis&mauris=odio&laoreet=donec&ut=vitae&rhoncus=nisi&aliquet=nam&pulvinar=ultrices&sed=libero&nisl=non&nunc=mattis&rhoncus=pulvinar&dui=nulla&vel=pede&sem=ullamcorper&sed=augue&sagittis=a&nam=suscipit&congue=nulla&risus=elit&semper=ac&porta=nulla&volutpat=sed&quam=vel&pede=enim&lobortis=sit&ligula=amet&sit=nunc&amet=viverra&eleifend=dapibus&pede=nulla&libero=suscipit&quis=ligula&orci=in&nullam=lacus&molestie=curabitur&nibh=at&in=ipsum&lectus=ac');
insert into app.garment_url (garment_id, garment_url) values (2, 'http://google.it/erat/fermentum/justo/nec/condimentum.json?massa=semper&volutpat=sapien&convallis=a&morbi=libero&odio=nam&odio=dui&elementum=proin&eu=leo&interdum=odio&eu=porttitor&tincidunt=id&in=consequat&leo=in&maecenas=consequat&pulvinar=ut&lobortis=nulla&est=sed&phasellus=accumsan');

--Outfit Garment (# of relations: )
insert into app.outfit_garment (outfit_id, garment_id) values (2, 8);
insert into app.outfit_garment (outfit_id, garment_id) values (2, 9);
insert into app.outfit_garment (outfit_id, garment_id) values (4, 2);
insert into app.outfit_garment (outfit_id, garment_id) values (2, 2);
insert into app.outfit_garment (outfit_id, garment_id) values (4, 7);
insert into app.outfit_garment (outfit_id, garment_id) values (3, 3);
insert into app.outfit_garment (outfit_id, garment_id) values (1, 2);
insert into app.outfit_garment (outfit_id, garment_id) values (3, 5);
insert into app.outfit_garment (outfit_id, garment_id) values (1, 3);
insert into app.outfit_garment (outfit_id, garment_id) values (1, 5);
