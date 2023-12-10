import math
import firebase_admin
import firebase_admin.credentials as credentials
from firebase_admin import db
from tqdm import tqdm

firebase_admin.initialize_app(
    credential=credentials.Certificate('group15finalproj-9916e796488c.json')
)

db = db.reference(url = 'https://group15finalproj-default-rtdb.firebaseio.com/')

report_ref = db.child('report')

import firebase_admin.firestore as firestore

# firestore_app = firebase_admin.initialize_app(
#     credential=credentials.Certificate('group15finalproj-9916e796488c.json')
# )

firestore_db = firestore.client()

# Get users from firestore
users = firestore_db.collection('users').get()

print(users)

for user in users:
    print(user.to_dict().keys())

list_of_users = []
for user in users:
    list_of_users.append(user.to_dict().get('userName'))

print(list_of_users)

import json

print(json.dumps(report_ref.get(), indent=2))

# Create a list of 100 reports with random info inside of a 10 mile radius of 42.33251869702252, -71.10496781543584
import random
import datetime
import string

def generate_random_string(length):
    characters = string.ascii_letters 
    return ''.join(random.choice(characters) for _ in range(length))

def generate_random_sentence():
    sentence = ''
    for i in range(random.randint(3, 10)):
        sentence += generate_random_string(20) + ' '
    return sentence

report_types = [
    "Crime",
    "Lack of visibility/Darkness",
    "People loitering"
]

import geopy
import pandas as pd

geoCodeKey = json.load(open('geo_coding_key.json', 'r'))['key']

def getZipcode(latitude, longitude):
    df = pd.DataFrame({'Lat': [latitude], 'Long': [longitude]})
    locator = geopy.GoogleV3(api_key=geoCodeKey)
    location = locator.reverse(str(latitude) + ', ' + str(longitude))
    address_components = location.raw["address_components"]
    postal_code = None
    for component in address_components:
        if "postal_code" in component["types"]:
            postal_code = component["long_name"]
            break
    return postal_code

def getZipcodeFromLocation(location):
    address_components = location.raw["address_components"]
    postal_code = None
    for component in address_components:
        if "postal_code" in component["types"]:
            postal_code = component["long_name"]
            break
    return postal_code

from dataclasses import dataclass

# Stores the address of a report
@dataclass
class GoogleAddress:
    """ Stores the address of a report """

    address_components: dict
    formatted_address: str
    zipcode: str
    latitude: float
    longitude: float

    def __init__(self, location):
        self.address_components = location.raw["address_components"]
        self.formatted_address = location.raw["formatted_address"]
        self.zipcode = self.get_zipcode()
        self.latitude = location.latitude
        self.longitude = location.longitude

    def get_zipcode(self):
        for component in self.address_components:
            if "postal_code" in component["types"]:
                return component["long_name"]
        return None
    
    def get_state(self):
        for component in self.address_components:
            if "administrative_area_level_1" in component["types"]:
                return component["long_name"]
        return None
    
    def get_city(self):
        for component in self.address_components:
            if "locality" in component["types"]:
                return component["long_name"]
        return None
    
    def __str__(self):
        return self.formatted_address + ' ' + str(self.latitude) + ' ' + str(self.longitude)

from geopy.extra.rate_limiter import RateLimiter

custom_lat = 42.33251869702252
custom_long = -71.10496781543584

northeasternLat = 42.34017461891752
northeasternLong = -71.08838892940447

centerLat = northeasternLat
centerLong = northeasternLong

variance_range = 0.001

NUM_REPORTS = 5

for i in tqdm(range(NUM_REPORTS)):

    randomLat = centerLat + random.uniform(-variance_range, variance_range)
    randomLong = centerLong + random.uniform(-variance_range, variance_range)

    # Get the address of the report
    locator = geopy.GoogleV3(api_key=geoCodeKey)
    reverse = RateLimiter(locator.reverse, min_delay_seconds=0.1)
    location = reverse(str(randomLat) + ', ' + str(randomLong))
    address = GoogleAddress(location)

    # print(address.get_city())
    # print(address.get_state())

    report = {
        'city': address.get_city(),
        'detail': generate_random_sentence(),
        'latitude': randomLat,
        'longitude': randomLong,
        'state': address.get_state(),
        'street_address': address.formatted_address,
        # time as random utc timestamp within last 24 hours
        'time': math.floor(datetime.datetime.utcnow().timestamp() - random.randint(0, 86400)),
        'type': report_types[random.randint(0, len(report_types) - 1)],
        'username': list_of_users[random.randint(0, len(list_of_users) - 1)],
        # Get the zipcode from the latitude and longitude
        'zipcode': address.get_zipcode(),
        'testing': True
        }

    # If any field in the report is None, skip the report
    if None in report.values():
        continue

    # Push the report to the database
    report_ref.push(report)
    # report_ref.child('report' + str(i)).set(report)
    # print(json.dumps(report, indent=2))

# Print the reports in the database
