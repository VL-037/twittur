# Account Controller

- [Get Accounts](#get-accounts)
- [Get Account by Username](#get-account-by-username)
- [Create Account](#create-account)
- [Update Account](#update-account)
- [Follow Account](#follow-account)
- [Unfollow Account](#unfollow-account)
- [Get Followers](#get-followers)
- [Get Following](#get-following)

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
      "tweetsCount": 0,
      "followersCount": 0,
      "followingCount": 0
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
      "tweetsCount": 0,
      "followersCount": 0,
      "followingCount": 0
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
      "tweetsCount": 0,
      "followersCount": 0,
      "followingCount": 0
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
      "tweetsCount": 0,
      "followersCount": 0,
      "followingCount": 0
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
      "tweetsCount": 0,
      "followersCount": 0,
      "followingCount": 0
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
    "tweetsCount": 15,
    "followersCount": 2,
    "followingCount": 0
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
  "emailAddress": "john.doe@example.com",
  "phoneNumber": "+621234567890",
  "password": "my password",
  "confirmPassword": "my password"
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
    "tweetsCount": 0,
    "followersCount": 0,
    "followingCount": 0
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

- response sample &rarr; `phoneNumber` invalid

```json
{
  "code": 400,
  "status": "BAD_REQUEST",
  "error": "phone number is not valid"
}
```

- response sample &rarr; `password` length < 10

```json
{
  "code": 400,
  "status": "BAD_REQUEST",
  "error": "password minimal length is 10"
}
```

- response sample &rarr; `password` & `confirmPassword` different

```json
{
  "code": 400,
  "status": "BAD_REQUEST",
  "error": "confirm password is different with password"
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

## <a name="update-account"></a> Update Account

**Http Method**: `PUT`

**Endpoint**: `/api/v1/accounts/@{username}`

**Request Param**: -

**Request Body**:

```json
{
  "username": "johndoe",
  "accountName": "johndoe123",
  "bio": "I'm a graphic designer",
  "emailAddress": "john.doe@example.com",
  "phoneNumber": "",
  "oldPassword": "old password",
  "newPassword": "new password",
  "confirmNewPassword": "new password"
}
```

**Response**:

- response sample

```json
{
  "code": 200,
  "status": "OK",
  "data": {
    "id": "886e0286-536b-4fd3-ad7f-3b1916c222a6",
    "createdDate": "2023-03-23T04:31:33.227578",
    "createdBy": "system",
    "updatedDate": "2023-03-23T04:31:55.1451171",
    "updatedBy": "886e0286-536b-4fd3-ad7f-3b1916c222a6",
    "username": "johndoe",
    "accountName": "johndoe123",
    "bio": "I'm a graphic designer",
    "tweetsCount": 0,
    "followersCount": 0,
    "followingCount": 0
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

- response sample &rarr; `phoneNumber` invalid

```json
{
  "code": 400,
  "status": "BAD_REQUEST",
  "error": "phone number is not valid"
}
```

- response sample &rarr; `password` length < 10

```json
{
  "code": 400,
  "status": "BAD_REQUEST",
  "error": "password minimal length is 10"
}
```

- response sample &rarr; `password` & `confirmPassword` different

```json
{
  "code": 400,
  "status": "BAD_REQUEST",
  "error": "confirm password is different with password"
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

## <a name="follow-account"></a> Follow Account

**Http Method**: `POST`

**Endpoint**: `/api/v1/accounts/_follow`

**Request Param**: -

**Request Body**:

```json
{
  "followerId": "f52527c7-4080-4f92-93b9-646b00dcac11",
  "followedId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8"
}
```

**Response**:

- response sample:

```json
{
  "code": 200,
  "status": "OK"
}
```

- response sample &rarr; `followerId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "follower account not found"
}
```

- response sample &rarr; `followedId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "follower account not found"
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

## <a name="unfollow-account"></a> Unfollow Account

**Http Method**: `POST`

**Endpoint**: `/api/v1/accounts/_unfollow`

**Request Param**: -

**Request Body**:

```json
{
  "followerId": "f52527c7-4080-4f92-93b9-646b00dcac11",
  "followedId": "bb36c86d-7f03-4cc8-8798-47cd7427cba8"
}
```

**Response**:

- response sample:

```json
{
  "code": 200,
  "status": "OK"
}
```

- response sample &rarr; `followerId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "follower account not found"
}
```

- response sample &rarr; `followedId` not found

```json
{
  "code": 404,
  "status": "NOT_FOUND",
  "error": "follower account not found"
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

## <a name="get-followers"></a> Get Followers

**Http Method**: `GET`

**Endpoint**: `/api/v1/accounts/@{username}/followers`

**Request Param**:

| Request Param | Data Type        |
|---------------|------------------|
| pageNumber    | int, default: 0  |
| pageSize      | int, default: 10 |

**Request Body**: -

**Response**:

- response sample &rarr; `/@johndoe123/followers`

```json
{
  "code": 200,
  "status": "OK",
  "content": [
    {
      "id": "6170b39a-8a94-430e-9976-79c4d9683988",
      "createdDate": "2023-03-21T09:46:43.946613",
      "createdBy": "system",
      "updatedDate": "2023-03-21T09:46:43.946613",
      "updatedBy": "system",
      "username": "janesmith456",
      "accountName": "Jane's Account"
    },
    {
      "id": "f52527c7-4080-4f92-93b9-646b00dcac11",
      "createdDate": "2023-03-21T09:46:44.016045",
      "createdBy": "system",
      "updatedDate": "2023-03-21T09:46:44.016045",
      "updatedBy": "system",
      "username": "robertjohnson789",
      "accountName": "Robert's Account"
    }
  ],
  "pageMetaData": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalRecords": 2
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

## <a name="get-following"></a> Get Following

**Http Method**: `GET`

**Endpoint**: `/api/v1/accounts/@{username}/following`

**Request Param**:

| Request Param | Data Type        |
|---------------|------------------|
| pageNumber    | int, default: 0  |
| pageSize      | int, default: 10 |

**Request Body**: -

**Response**:

- response sample &rarr; `/@janesmith456/following`

```json
{
  "code": 200,
  "status": "OK",
  "content": [
    {
      "id": "bb36c86d-7f03-4cc8-8798-47cd7427cba8",
      "createdDate": "2023-03-21T09:46:43.874639",
      "createdBy": "system",
      "updatedDate": "2023-03-21T09:46:43.874639",
      "updatedBy": "system",
      "username": "johndoe123",
      "accountName": "John's Account"
    }
  ],
  "pageMetaData": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalRecords": 1
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
