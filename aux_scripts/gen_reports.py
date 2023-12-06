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
user_ref = db.child('users')

import json

print(json.dumps(report_ref.get(), indent=2))

# Report format
    # {
    # "city": "t2",
    # "detail": "t2",
    # "latitude": 42.361145,
    # "longitude": -71.057083,
    # "state": "t2",
    # "street_address": "t2",
    # "time": "timestamp in UTC of incident",
    # "type": "Red Yellow Orange",
    # "username": "user1",
    # "zipcode": "t2"
#   }

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

for i in tqdm(range(2000)):

    # if i == 5:
    #     break

    randomLat = 42.33251869702252 + random.uniform(-0.1, 0.1)
    randomLong = -71.10496781543584 + random.uniform(-0.1, 0.1)

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
        'username': user_ref.get()['user' + str(random.randint(1, 99))]['userName'],
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
