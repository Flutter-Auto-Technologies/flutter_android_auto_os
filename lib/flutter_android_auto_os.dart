
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAndroidAutoOs {
  static const MethodChannel _channel = MethodChannel('flutter_android_auto_os');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
