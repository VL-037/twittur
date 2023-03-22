# Direct Message Controller

- [Get Direct Messages](#get-direct-messages)
- [Send Direct Message](#send-direct-message)

---

## <a name="get-direct-messages"></a> Get Direct Messages

**Http Method**: `GET`

**Endpoint**: `/api/v1/direct-messages/{senderId}/{recipientId}`

**Request Param**:

| Request Param | Data Type       |
|---------------|-----------------|
| pageNumber    | int, default: 0 |

**Request Body**: -

**Response**:

- response sample &rarr; `/bb36c86d-7f03-4cc8-8798-47cd7427cba8/6170b39a-8a94-430e-9976-79c4d9683988?pageNumber=0`

```json
{
  "code": 200,
  "status": "OK",
  "content": [
    {
      "id": "3d1c0046-caa4-4b62-b163-c2c1f4115211",
      "createdDate": "2023-03-21T11:23:00.182863",
      "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "updatedDate": "2023-03-21T11:23:00.182863",
      "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "senderId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "recipientId": "6170b39a-8a94-430e-9976-79c4d9683988",
      "message": "Yeah, it's so sad she have to go",
      "status": "DELIVERED"
    },
    {
      "id": "e8ca45ac-ea95-4597-8d0a-f45fc41f9f34",
      "createdDate": "2023-03-21T11:23:41.66156",
      "createdBy": "6170b39a-8a94-430e-9976-79c4d9683988",
      "updatedDate": "2023-03-21T11:23:41.66156",
      "updatedBy": "6170b39a-8a94-430e-9976-79c4d9683988",
      "senderId": "6170b39a-8a94-430e-9976-79c4d9683988",
      "recipientId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "message": "we have to prepare farewell party for her",
      "status": "DELIVERED"
    },
    {
      "id": "5cab94e8-0fe3-43bd-a692-57c673280b5e",
      "createdDate": "2023-03-21T11:24:02.542833",
      "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "updatedDate": "2023-03-21T11:24:02.542833",
      "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "senderId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "recipientId": "6170b39a-8a94-430e-9976-79c4d9683988",
      "message": "agreed, meet me at 12",
      "status": "DELIVERED"
    },
    {
      "id": "c3cb53f2-0fd2-42b7-9b31-1a2f84154284",
      "createdDate": "2023-03-21T11:24:07.502352",
      "createdBy": "6170b39a-8a94-430e-9976-79c4d9683988",
      "updatedDate": "2023-03-21T11:24:07.502352",
      "updatedBy": "6170b39a-8a94-430e-9976-79c4d9683988",
      "senderId": "6170b39a-8a94-430e-9976-79c4d9683988",
      "recipientId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "message": "got it",
      "status": "DELIVERED"
    }
  ]
}
```

- response sample &rarr; `senderId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "sender account not found"
}
```

- response sample &rarr; `recipientId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "recipient account not found"
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

## <a name="send-direct-message"></a> Send Direct Message

**Http Method**: `POST`

**Endpoint**: `/api/v1/direct-messages/{senderId}/{recipientId}`

**Request Param**: -

**Request Body**:

```json
{
  "message": "agreed, meet me at 12"
}
```

**Response**:

- response sample &rarr; `/bb36c86d-7f03-4cc8-8798-47cd7427cba8/6170b39a-8a94-430e-9976-79c4d9683988`

```json
{
  "code": 200,
  "status": "OK",
  "data": {
    "id": "acab74ad-dcb5-4cad-a5b3-982e54f00e09",
    "createdDate": "2023-03-23T04:58:41.0882867",
    "createdBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
    "updatedDate": "2023-03-23T04:58:41.0882867",
    "updatedBy": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
    "senderId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
    "recipientId": "6170b39a-8a94-430e-9976-79c4d9683988",
    "message": "agreed, meet me at 12",
    "status": "DELIVERED"
  }
}
```

- response sample &rarr; `senderId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "sender account not found"
}
```

- response sample &rarr; `recipientId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "recipient account not found"
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
