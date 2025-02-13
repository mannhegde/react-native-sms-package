# react-native-sms-package

A high-performance React Native package for Android to check permissions, request access, send, and
read SMS messages. Supports filtering messages by start and end date for optimized retrieval.

## Installation

```sh
npm install react-native-sms-package
```
### Additional Steps Android (if required)
In your application's ```MainApplication.kt```, override the ```getPackages``` method to include SmsPackage.

```kotlin
import com.smspackage.SmsPackage

override fun getPackages():List<ReactPackage> {
     return PackageList(this).packages.apply {
       // ...other packages
       add(new SmsPackage());
     }
}
```


## Usage

```js
import { sendSms, sendSmsToMultipleRecipients, sendSmsManually, fetchAllSMS, fetchSMSForPeriod, fetchLatestSMS } from 'react-native-sms-package';


const result = await sendSms({
  recipient: '9876543210', // string
  payload: 'hey i found new package, check it out', // string
  subscriptionId: 1, //optional number. will pick default subscription id if not provided. Works only for android.
  shouldVerifySuccessfulSend: true, // optional boolean. Default is set to false
});

const result = await sendSmsToMultipleRecipients({
  recipients: ['9876543210', '1234567890'], // string[]
  payload: 'hey i found new package, check it out', // string
  subscriptionId: 1, //optional number. will pick default subscription id if not provided. Works only for android.
  shouldVerifySuccessfulSend: true, // optional boolean. Default is set to false
});

// Android only methods

const result = await sendSmsManually({
  recipient: '9876543210', //string
  payload: 'hey i found new package, check it out', // string
});

const result = await fetchAllSMS({
  includePersonalMessages: false, // optional boolean. Default is set to false
  order: 'desc', // optional 'desc' | 'asc'.  Default is set to desc
});

const result = await fetchSMSForPeriod({
  startDateTime: '2024-02-13T13:40:11+00:00', //Date string. iso8601 or YYYY-MM-DD
  endDateTime: '2025-02-13T13:40:11+00:00', //Date string. iso8601 or YYYY-MM-DD
  includePersonalMessages: false, // optional boolean. Default is set to false
  order: 'desc', // optional 'desc' | 'asc'.  Default is set to desc
});
const result = await fetchLatestSMS({
  limit: 1, // Optional number. Default is set to 1
});

//Note:
//includePersonalMessages flag is for including reading sms from mobile numbers (Ex: +91-9876543210). When set to false (default) only marketing sms will be retrivied.

```
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the
development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
