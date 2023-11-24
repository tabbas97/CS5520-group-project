from firebase_admin import db
import firebase_admin
import firebase_admin.credentials as credentials

firebase_admin.initialize_app(
    credential=credentials.Certificate('group15finalproj-9916e796488c.json')
)

db = db.reference(url = 'https://group15finalproj-default-rtdb.firebaseio.com/')

# user format = {
    # dateOfBirth: string
    # fullName : string
    # password : string
    # userName : string
    # friendIds : [string] # constraints: must be a list of strings of user ids that exist in the database
# }

import json

user_ref = db.child('users')
print(json.dumps(user_ref.get(), indent=2))

# Delete all users whose key does not start with user
# users = user_ref.get()
# for user in users:
#     if not user.startswith('user'):
#         user_ref.child(user).delete()

# Create a list of 100 users with random info with 5 random friends each
import random

for i in range(4, 100):

    # delete user
    # user_ref.child('user' + str(i)).delete()

    # Generate random user info
    user = {
        'dateOfBirth': str(random.randint(1, 12)) + '/' + str(random.randint(1, 28)) + '/' + str(random.randint(1900, 2021)),
        'fullName': 'User ' + str(i),
        'password': 'password',
        'userName': 'user' + str(i),
        'friendIds': []
    }

    # Add 5 random friends
    for j in range(5):
        user['friendIds'].append("user" + str(random.randint(0, 99)))

    # Add the user to the database with the key user + i
    user_ref.child('user' + str(i)).set(user)

# Print the users in the database
users = user_ref.get()
for user in users:
    print(users[user])