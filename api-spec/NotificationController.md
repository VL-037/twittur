# Notification Controller

- [Get Notifications](#get-notifications)

---

## <a name="get-notifications"></a> Get Notifications

**Http Method**: `GET`

**Endpoint**: `/api/v1/notifications`

**Request Param**:

| Request Param | Data Type       |
|---------------|-----------------|
| pageNumber    | int, default: 0 |

**Request Body**:

```json
{
  "recipientId": "6170b39a-8a94-430e-9976-79c4d9683988"
}
```

**Response**:

- response sample:

```json
{
  "code": 200,
  "status": "OK",
  "content": [
    {
      "id": "03aa5ef4-90d5-494e-a96b-fec2a43b74c2",
      "createdDate": "2023-03-23T04:50:33.730502",
      "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "updatedDate": "2023-03-23T04:50:33.730502",
      "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "title": "John's Account Tweeted:",
      "message": "For my followers <3",
      "imageUrl": "IMAGE_URL",
      "redirectUrl": "/@johndoe123/tweets/fb85cc70-d0cc-410c-92ef-8b09d9354324",
      "type": "NEW_TWEET",
      "hasRead": false
    },
    {
      "id": "85e1847e-7fbf-4210-bf2c-2e5c2655798a",
      "createdDate": "2023-03-23T04:50:01.513913",
      "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "updatedDate": "2023-03-23T04:50:01.513913",
      "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "title": "John's Account Tweeted:",
      "message": "For my followers <3",
      "imageUrl": "IMAGE_URL",
      "redirectUrl": "/@johndoe123/tweets/4e6d5563-4a48-48dc-8187-b9127859a57d",
      "type": "NEW_TWEET",
      "hasRead": false
    },
    {
      "id": "dd5239d7-2e8d-4c89-bc1c-108314d1e880",
      "createdDate": "2023-03-22T03:16:51.485964",
      "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "updatedDate": "2023-03-22T03:16:51.485964",
      "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "title": "John's Account Tweeted:",
      "message": "For my followers <3",
      "imageUrl": "IMAGE_URL",
      "redirectUrl": "/@johndoe123/tweets/01ca5281-42de-49cd-b96b-382baefee61e",
      "type": "NEW_TWEET",
      "hasRead": false
    },
    {
      "id": "5b22d4c5-36a5-499e-bfed-2e6ecbc0b7e6",
      "createdDate": "2023-03-22T03:16:19.371584",
      "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "updatedDate": "2023-03-22T03:16:19.371584",
      "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "title": "John's Account Tweeted:",
      "message": "Do you know that water is liquid? It's not a gas XD",
      "imageUrl": "IMAGE_URL",
      "redirectUrl": "/@johndoe123/tweets/c2a86195-8e40-402c-ba1c-16308ee672fa",
      "type": "NEW_TWEET",
      "hasRead": false
    }
  ],
  "pageMetaData": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalRecords": 4
  }
}
```

- response sample &rarr; empty `content`

```json
{
  "code": 200,
  "status": "OK",
  "content": [],
  "pageMetaData": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalRecords": 0
  }
}
```

- response sample &rarr; `recipientId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "account not found"
}
```

- response sample &rarr; Server Error

```json
{
  "code": 503,
  "status": "SERVICE_UNAVAILABLE",
  "error": "service temporarily unavailable"
}
```
