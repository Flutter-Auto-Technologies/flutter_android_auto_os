import Flutter
import UIKit

public class SwiftFlutterAndroidAutoOsPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_android_auto_os", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterAndroidAutoOsPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
