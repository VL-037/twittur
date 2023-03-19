# Account Controller

- [Get Accounts](#get-accounts)
- [Get Account by Username](#get-account-by-username)
- [Create Account](#create-account)

---

## <a name="get-accounts"></a> Get Accounts

**Http Method**: `GET`

**Endpoint**: `/api/v1/accounts`

**Request Param**:

| Request Param | Data Type        |
|---------------|------------------|
| pageNumber    | int, default: 0  |
| pageSize      | int, default: 10 |

**Request Body**: -

**Response**:

- response sample &rarr; `?pageNumber=0&pageSize=5`

```json
{
  "code": 200,
  "status": "OK",
  "error": null,
  "content": [
    {
      "id": "c827da34-c58b-4a4b-96c3-fd0a9ea55fe5",
      "createdDate": "2023-03-18T07:32:40.823+00:00",
      "createdBy": "system",
      "updatedDate": "2023-03-18T07:32:40.823+00:00",
      "updatedBy": "system",
      "username": "johndoe",
      "accountName": "johndoe123",
      "bio": "I'm a software engineer",
      "tweetsCount": 0
    },
    {
      "id": "83f94529-2d8d-4941-bdab-1f2ac91b5f4d",
      "createdDate": "2023-03-18T07:32:40.919+00:00",
      "createdBy": "system",
      "updatedDate": "2023-03-18T07:32:40.919+00:00",
      "updatedBy": "system",
      "username": "janesmith",
      "accountName": "janesmith123",
      "bio": "I'm a graphic designer",
      "tweetsCount": 0
    },
    {
      "id": "7af23582-7d04-4caa-8e22-a21c94ea3178",
      "createdDate": "2023-03-18T07:32:40.920+00:00",
      "createdBy": "system",
      "updatedDate": "2023-03-18T07:32:40.920+00:00",
      "updatedBy": "system",
      "username": "michaelbrown",
      "accountName": "michaelbrown123",
      "bio": "I'm a sales manager",
      "tweetsCount": 0
    },
    {
      "id": "b923270e-9b62-409f-ae9a-09c642865abc",
      "createdDate": "2023-03-18T07:32:40.920+00:00",
      "createdBy": "system",
      "updatedDate": "2023-03-18T07:32:40.920+00:00",
      "updatedBy": "system",
      "username": "sarahjohnson",
      "accountName": "sarahjohnson123",
      "bio": "I'm a journalist",
      "tweetsCount": 0
    },
    {
      "id": "c5d57ed5-4737-418d-bb97-ba4dea72bb3c",
      "createdDate": "2023-03-18T07:32:40.920+00:00",
      "createdBy": "system",
      "updatedDate": "2023-03-18T07:32:40.920+00:00",
      "updatedBy": "system",
      "username": "davidlee",
      "accountName": "davidlee123",
      "bio": "I'm a photographer",
      "tweetsCount": 0
    }
  ],
  "pageMetaData": {
    "pageNumber": 0,
    "pageSize": 5,
    "totalRecords": 15
  }
}
```

- response sample &rarr; empty `content`

```json
{
  "code": 200,
  "status": "OK",
  "error": null,
  "content": [],
  "pageMetaData": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalRecords": 0
  }
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

## <a name="get-account-by-username"></a> Get Account by Username

**Http Method**: `GET`

**Endpoint**: `/api/v1/accounts/@{username}`

**Request Param**:

| Request Param | Data Type        |
|---------------|------------------|
| pageNumber    | int, default: 0  |
| pageSize      | int, default: 10 |

**Request Body**: -

**Response**:

- response sample &rarr; `/@johndoe`

```json
{
  "code": 200,
  "status": "OK",
  "error": null,
  "data": {
    "id": "02815707-bb89-49e6-ba15-dad91f047312",
    "createdDate": "2023-03-18T07:52:55.584+00:00",
    "createdBy": "system",
    "updatedDate": "2023-03-18T07:52:55.584+00:00",
    "updatedBy": "system",
    "username": "johndoe",
    "accountName": "johndoe123",
    "bio": "I'm a software engineer",
    "tweetsCount": 15
  }
}
```

- response sample &rarr; `username` not found

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

## <a name="create-account"></a> Create Account

**Http Method**: `POST`

**Endpoint**: `/api/v1/accounts`

**Request Param**: -

**Request Body**:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "username": "johndoe",
  "accountName": "johndoe123",
  "bio": "I'm a software engineer",
  "emailAddress": "john.doe@example.com"
}
```

**Response**:

- response sample

```json
{
  "code": 200,
  "status": "OK",
  "error": null,
  "data": {
    "id": "27b22c67-cbaa-4cde-ab89-3c0d55bf7508",
    "createdDate": "2023-03-18T07:59:09.959+00:00",
    "createdBy": "system",
    "updatedDate": "2023-03-18T07:59:09.959+00:00",
    "updatedBy": "system",
    "username": "johndoe",
    "accountName": "johndoe123",
    "bio": "I'm a software engineer",
    "tweetsCount": 0
  }
}
```

- response sample &rarr; `username` taken

```json
{
  "code": 409,
  "status": "CONFLICT",
  "error": "username is taken"
}
```

- response sample &rarr; `email` exists

```json
{
  "code": 409,
  "status": "CONFLICT",
  "error": "email is associated with an account, please login or reset password"
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
