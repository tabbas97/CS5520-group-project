import firebase_admin
from firebase_admin import credentials, firestore

firebase_admin.initialize_app(
    credential=credentials.Certificate('group15finalproj-9916e796488c.json')
)

db = firestore.client()

user_ref = db.collection('users')
docs = user_ref.stream()
for doc in docs:
    print(f'{doc.id} => {doc.to_dict()}')
