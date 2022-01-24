import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAndroidAutoOs {
  static const MethodChannel _channel = MethodChannel('flutter_android_auto_os');
  static const EventChannel _carGearEventChannel = EventChannel('car_gear');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Stream get currentCarGear => _carGearEventChannel.receiveBroadcastStream();
}
