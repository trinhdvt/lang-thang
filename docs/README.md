## Note

All API endpoint start with a prefix **/api**

## Rate Limit Policy

### HTTP Response Headers

* **API-Rate-Limit-Remaining**: The number of remaining requests in the current time frame.

* **API-Rate-Limit-Retry-After-Seconds**: An amount of the expected time (in seconds) when the rate limit will reset.

* When client exceed the provided rate limit then an error will be sent back with `HTTP Status Code 429 (Too Many Requests)` and any further request will **be throttled** until rate limit reset.


| Endpoint                  | Path            | Limited By      | Rate Limit       |
| -----------------         |:------          | :------------:  | :-------         |
| Upload Image              | `/api/upload`   | IP Address      | 3 per 10 seconds |
| Authentication            | `/api/auth/*`   | IP Address      | 3 per 5 seconds  |
| Other (exclude above)     | `/api/**`       | IP Address      | 100 per minute   |


