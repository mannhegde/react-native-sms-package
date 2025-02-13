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
  smsRecipient,
  smsPayload,
  subscriptionId,
  shouldVerify = false,
}: {
  smsRecipient: string;
  smsPayload: string;
  subscriptionId?: string;
  shouldVerify?: boolean;
}) {
  return SmsPackage.sendSms(
    smsRecipient,
    smsPayload,
    subscriptionId,
    shouldVerify
  );
}

export function sendSmsToMultipleRecipients({
  smsRecipients,
  smsPayload,
  subscriptionId,
  shouldVerify = false,
}: {
  smsRecipients: string[];
  smsPayload: string;
  subscriptionId?: string;
  shouldVerify?: boolean;
}) {
  return SmsPackage.sendSmsToMultipleRecipients(
    smsRecipients,
    smsPayload,
    subscriptionId,
    shouldVerify
  );
}

export function sendSmsManually({
  smsRecipient,
  smsPayload,
}: {
  smsRecipient: string;
  smsPayload: string;
}) {
  return SmsPackage.sendSmsManually(smsRecipient, smsPayload);
}

export function fetchAllSMS({
  includePersonalSMS = false,
  order = 'desc',
}: {
  includePersonalSMS?: boolean;
  order?: 'desc' | 'asc';
}) {
  return SmsPackage.fetchAllSMS(includePersonalSMS, order);
}

export function fetchSMSForPeriod({
  startDateTime,
  endDateTime,
  includePersonalSMS = false,
  order = 'desc',
}: {
  startDateTime?: string;
  endDateTime?: string;
  includePersonalSMS?: boolean;
  order?: 'desc' | 'asc';
}) {
  return SmsPackage.fetchSMSForPeriod(
    startDateTime,
    endDateTime,
    includePersonalSMS,
    order
  );
}

export function fetchLatestSMS({ limit = 1 }: { limit?: number }) {
  return SmsPackage.fetchLatestSMS(limit);
}
