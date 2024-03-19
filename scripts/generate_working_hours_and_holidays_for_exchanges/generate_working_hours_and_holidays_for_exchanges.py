# https://github.com/gerrymanoim/exchange_calendars
import exchange_calendars as xcals
import json

def calendar_info(calendar_name: str) -> dict:
    exchange = xcals.get_calendar(calendar_name)

    calendar_info_dict = {
        "open": str(exchange.open_times[0][1]),
        "close": str(exchange.close_times[0][1])
    }

    holidays = []
    if exchange.regular_holidays is not None:
        holidays = [str(holiday).split(" ")[0] for holiday in exchange.regular_holidays.holidays(start="2024-01-01", end="2024-12-31")]
    
    calendar_info_dict["holidays"] = holidays

    return calendar_info_dict

if __name__ == "__main__":
    output_dict = dict()
    for calendar_name in xcals.get_calendar_names(include_aliases=False):
        if calendar_name in {"24/5", "24/7", "us_futures"}:
            continue
        output_dict[calendar_name] = calendar_info(calendar_name)
    
    print(json.dumps(output_dict, indent=4), file=open("working_hours_and_holidays_for_exchanges.json", "+w"))



