# post format
# {
#     title: "t1",
#    body: "t1",
#    username: "user1",
#    time: "timestamp in UTC of incident",
#    attached_reports: optional - [report_id1, report_id2, ...]
#    comments: optional - [comment_id1, comment_id2, ...]
#    +1s: optional - [username1, username2, ...]
# }

# Comment format
# {
#    body: "t1",
#    username: "user1",
#    time: "timestamp in UTC of incident",
# }

import random
import datetime
import firebase_admin
import firebase_admin.credentials as credentials
from firebase_admin import db
import json
import geopy
import pandas as pd
import string

firebase_admin.initialize_app(
    credential=credentials.Certificate('group15finalproj-9916e796488c.json')
)

db = db.reference(url = 'https://group15finalproj-default-rtdb.firebaseio.com/')
post_ref = db.child('post')
user_ref = db.child('users')
report_ref = db.child('report')

def generate_random_string(length):
    characters = string.ascii_letters 
    return ''.join(random.choice(characters) for _ in range(length))

def generate_random_sentence():
    sentence = ''
    for i in range(random.randint(3, 10)):
        sentence += generate_random_string(20) + ' '
    return sentence

def generate_random_comment():
    return generate_random_sentence()

def generate_random_post():
    # generate random post
    post = {}
    post['title'] = generate_random_sentence()
    post['body'] = generate_random_sentence()
    post['username'] = random.choice(list(user_ref.get().keys()))
    # Time can be anywhere from 1 day ago to 1 hour ago - constraint - if report is tagges, time must be after report time
    post['attached_reports'] = []
    for i in range(random.randint(0, 1)):
        post['attached_reports'].append(random.choice(list(report_ref.get().keys())))

    if len(post['attached_reports']) == 0:
        maxBackTime = datetime.datetime.now() - datetime.timedelta(days=1)
    else:
        print((post['attached_reports'][0]))
        maxBackTime = max(
            datetime.datetime.now() - datetime.timedelta(days=1),
            datetime.datetime.fromtimestamp(post['attached_reports'][0]['time']) + datetime.timedelta(minutes=1)
        )

    post['time'] = maxBackTime.replace(tzinfo=datetime.timezone.utc).timestamp()
    post['comments'] = []
    for i in range(random.randint(0, 10)):
        post['comments'].append(generate_random_comment())
    post['+1s'] = []
    for i in range(random.randint(0, 10)):
        post['+1s'].append(random.choice(list(user_ref.get().keys())))
    return post

for i in range(100):
    print(i)
    generate_random_post()
    # print(generate_random_post())