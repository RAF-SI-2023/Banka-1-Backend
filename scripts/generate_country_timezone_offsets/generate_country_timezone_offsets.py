import requests
import json

def get_timezones(key: str):
    url = "http://api.timezonedb.com/v2.1/list-time-zone"
    format = "json"
    params = {
        "key": key,
        "format": format
    }

    try:
        resp = requests.get(url, params=params)
        
        if resp.status_code == 200:
            return json.dumps(json.loads(resp.content)["zones"], indent=4)
        else:
            print(f"Request failed with status code: {resp.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        print(f"Request error: {e}")
        return None


if __name__ == "__main__":
    #ditsma2020rn@raf.rs account api key
    api_key = "PPLL424SL29Q"
    response_content = get_timezones(api_key)

    if response_content:
        print(response_content, file=open("country_timezone_offsets.json", "+w"))
