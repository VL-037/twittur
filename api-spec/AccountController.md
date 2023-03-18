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
      "tweets": [],
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
      "tweets": [],
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
      "tweets": [],
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
      "tweets": [],
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
      "tweets": [],
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

- response sample &arr; empty `content`

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
    "tweets": [
      {
        "id": "56ccba0c-4294-41f1-9891-c6706f3e977e",
        "createdDate": "2023-03-18T07:53:18.133+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:18.133+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "The sun is shining and the birds are singing. What a beautiful day!"
      },
      {
        "id": "a522132c-672d-45fc-a522-93036dae3ba8",
        "createdDate": "2023-03-18T07:53:18.134+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:18.134+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "I just finished a great book and I'm already looking for my next read."
      },
      {
        "id": "cd975445-ca88-44a8-8876-538c00d43c92",
        "createdDate": "2023-03-18T07:53:18.134+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:18.134+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "I'm so grateful for my friends and family. They always know how to make me smile."
      },
      {
        "id": "e759dc69-6ac3-4210-afde-8a14b28e8f13",
        "createdDate": "2023-03-18T07:53:18.139+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:18.139+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "Sometimes the best thing to do is just take a deep breath and let it go."
      },
      {
        "id": "70d64519-243a-480d-87d3-53500febe28d",
        "createdDate": "2023-03-18T07:53:18.139+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:18.139+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "I can't believe how fast time flies. It feels like just yesterday I was starting this job."
      },
      {
        "id": "adfc6ae5-68ef-46ad-88af-cc23efb6e8cd",
        "createdDate": "2023-03-18T07:53:19.833+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:19.833+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "The sun is shining and the birds are singing. What a beautiful day!"
      },
      {
        "id": "df4d8793-eb84-4d57-90d3-201f26627503",
        "createdDate": "2023-03-18T07:53:19.833+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:19.833+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "I just finished a great book and I'm already looking for my next read."
      },
      {
        "id": "aead54db-e709-413e-bac1-0ecd997c1009",
        "createdDate": "2023-03-18T07:53:19.833+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:19.833+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "I'm so grateful for my friends and family. They always know how to make me smile."
      },
      {
        "id": "1f11dc08-0539-4e68-98b5-6df67834ca44",
        "createdDate": "2023-03-18T07:53:19.833+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:19.833+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "Sometimes the best thing to do is just take a deep breath and let it go."
      },
      {
        "id": "7acceb1a-59cc-44ff-b11f-42adc318fd73",
        "createdDate": "2023-03-18T07:53:19.833+00:00",
        "createdBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "updatedDate": "2023-03-18T07:53:19.833+00:00",
        "updatedBy": "02815707-bb89-49e6-ba15-dad91f047312",
        "message": "I can't believe how fast time flies. It feels like just yesterday I was starting this job."
      }
    ],
    "tweetsCount": 15
  }
}
```

- response sample &rarr; empty `tweets`

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
    "tweets": [],
    "tweetsCount": 15
  }
}
```

- response sample &arr; `username` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "account not found"
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
    "tweets": [],
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
