import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-sms-package' doesn't seem to be linked. Make sure:\n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SmsPackage = NativeModules.SmsPackage
  ? NativeModules.SmsPackage
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

// Exporting the native methods
export function sendSms({
  recipient,
  payload,
  subscriptionId,
  shouldVerifySuccessfulSend = false,
}: {
  recipient: string;
  payload: string;
  subscriptionId?: string;
  shouldVerifySuccessfulSend?: boolean;
}) {
  return SmsPackage.sendSms(
    recipient,
    payload,
    subscriptionId,
    shouldVerifySuccessfulSend
  );
}

export function sendSmsToMultipleRecipients({
  recipients,
  payload,
  subscriptionId,
  shouldVerifySuccessfulSend = false,
}: {
  recipients: string[];
  payload: string;
  subscriptionId?: string;
  shouldVerifySuccessfulSend?: boolean;
}) {
  return SmsPackage.sendSmsToMultipleRecipients(
    recipients,
    payload,
    subscriptionId,
    shouldVerifySuccessfulSend
  );
}

export function sendSmsManually({
  recipient,
  payload,
}: {
  recipient: string;
  payload: string;
}) {
  return SmsPackage.sendSmsManually(recipient, payload);
}

export function fetchAllSMS({
  includePersonalMessages = false,
  order = 'desc',
}: {
  includePersonalMessages?: boolean;
  order?: 'desc' | 'asc';
}) {
  return SmsPackage.fetchAllSMS(includePersonalMessages, order);
}

export function fetchSMSForPeriod({
  startDateTime,
  endDateTime,
  includePersonalMessages = false,
  order = 'desc',
}: {
  startDateTime: string;
  endDateTime: string;
  includePersonalMessages?: boolean;
  order?: 'desc' | 'asc';
}) {
  return SmsPackage.fetchSMSForPeriod(
    startDateTime,
    endDateTime,
    includePersonalMessages,
    order
  );
}

export function fetchLatestSMS({ limit = 1 }: { limit?: number }) {
  return SmsPackage.fetchLatestSMS(limit);
}
