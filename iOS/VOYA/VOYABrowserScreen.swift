import SwiftUI
import WebKit
import UIKit

private enum VOYAConstants {
    static let site = URL(string: "https://voya-app.funpony.chatgpt.site/")!
    static let siteHost = "voya-app.funpony.chatgpt.site"
    static let offlineHost = "offline.voya.invalid"
    static let background = Color(red: 245 / 255, green: 243 / 255, blue: 238 / 255)
    static let lime = Color(red: 200 / 255, green: 230 / 255, blue: 74 / 255)
}

final class VOYABrowserState: ObservableObject {
    @Published var loading = true
    @Published var offline = false
    @Published var reloadNumber = 0

    func retry() {
        offline = false
        loading = true
        reloadNumber += 1
    }
}

struct VOYABrowserScreen: View {
    @StateObject private var state = VOYABrowserState()

    var body: some View {
        ZStack(alignment: .top) {
            VOYAConstants.background.ignoresSafeArea()
            VOYAWebView(state: state)
                .ignoresSafeArea(edges: .bottom)

            if state.loading {
                ProgressView()
                    .tint(VOYAConstants.lime)
                    .padding(12)
                    .background(VOYAConstants.background.opacity(0.94), in: RoundedRectangle(cornerRadius: 12))
                    .padding(.top, 16)
                    .accessibilityLabel("Loading VOYA")
            }

            if state.offline {
                HStack(spacing: 12) {
                    Text("VOYA · OFFLINE PREVIEW")
                        .font(.system(size: 11, weight: .bold, design: .rounded))
                        .tracking(1)
                    Spacer()
                    Button("Retry") { state.retry() }
                        .font(.system(size: 12, weight: .bold, design: .rounded))
                        .foregroundStyle(.black)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 7)
                        .background(VOYAConstants.lime, in: Capsule())
                        .accessibilityLabel("Retry opening VOYA online")
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 9)
                .background(VOYAConstants.background)
            }
        }
    }
}

struct VOYAWebView: UIViewRepresentable {
    @ObservedObject var state: VOYABrowserState

    func makeCoordinator() -> Coordinator { Coordinator(self) }

    func makeUIView(context: Context) -> WKWebView {
        let config = WKWebViewConfiguration()
        config.websiteDataStore = .default() // Preserves login cookies between launches.
        config.allowsInlineMediaPlayback = true
        config.mediaTypesRequiringUserActionForPlayback = .all
        config.preferences.javaScriptCanOpenWindowsAutomatically = false

        let browser = WKWebView(frame: .zero, configuration: config)
        browser.navigationDelegate = context.coordinator
        browser.uiDelegate = context.coordinator
        browser.scrollView.contentInsetAdjustmentBehavior = .never
        browser.isOpaque = false
        browser.backgroundColor = UIColor(red: 245/255, green: 243/255, blue: 238/255, alpha: 1)
        browser.allowsBackForwardNavigationGestures = true
        context.coordinator.browser = browser
        context.coordinator.lastReloadNumber = state.reloadNumber
        browser.load(URLRequest(url: VOYAConstants.site, cachePolicy: .useProtocolCachePolicy))
        return browser
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        context.coordinator.parent = self
        if context.coordinator.lastReloadNumber != state.reloadNumber {
            context.coordinator.lastReloadNumber = state.reloadNumber
            context.coordinator.isFallback = false
            uiView.load(URLRequest(url: VOYAConstants.site, cachePolicy: .reloadIgnoringLocalCacheData))
        }
    }

    final class Coordinator: NSObject, WKNavigationDelegate, WKUIDelegate {
        var parent: VOYAWebView
        weak var browser: WKWebView?
        var lastReloadNumber = 0
        var isFallback = false

        init(_ parent: VOYAWebView) { self.parent = parent }

        private func isTrusted(_ url: URL?) -> Bool {
            guard let url = url else { return false }
            return url.scheme == "https" && url.host?.lowercased() == VOYAConstants.siteHost
        }

        private func loadFallback(in browser: WKWebView) {
            guard !isFallback else { return }
            isFallback = true
            parent.state.offline = true
            parent.state.loading = false
            if let path = Bundle.main.path(forResource: "offline", ofType: "html"),
               let html = try? String(contentsOfFile: path, encoding: .utf8) {
                browser.loadHTMLString(html, baseURL: URL(string: "https://offline.voya.invalid/"))
            } else {
                browser.loadHTMLString("<h1>VOYA</h1><p>Unable to connect.</p>", baseURL: nil)
            }
        }

        func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction,
                     decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
            guard let url = navigationAction.request.url else { decisionHandler(.cancel); return }
            if url.scheme == "voya" && url.host == "retry" {
                parent.state.retry()
                decisionHandler(.cancel)
                return
            }
            if isTrusted(url) || (url.scheme == "https" && url.host == VOYAConstants.offlineHost) || url.scheme == "about" {
                decisionHandler(.allow)
                return
            }
            // OAuth providers may need a dedicated ASWebAuthenticationSession integration.
            // Do not silently share credentials with unrelated embedded domains.
            if url.scheme == "https" || url.scheme == "mailto" || url.scheme == "tel" {
                UIApplication.shared.open(url)
            }
            decisionHandler(.cancel)
        }

        func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
            if !isFallback { parent.state.loading = true }
        }
        func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
            parent.state.loading = false
            if !isFallback { parent.state.offline = false }
        }
        func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
            // -999: cancelled navigation (e.g. redirected); not a connection failure.
            if (error as NSError).code != NSURLErrorCancelled { loadFallback(in: webView) }
        }
        func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
            if (error as NSError).code != NSURLErrorCancelled { loadFallback(in: webView) }
        }
        func webViewWebContentProcessDidTerminate(_ webView: WKWebView) {
            if !isFallback { webView.reload() }
        }

        func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration,
                     for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
            if navigationAction.targetFrame == nil {
                if let url = navigationAction.request.url, isTrusted(url) { webView.load(navigationAction.request) }
                else if let url = navigationAction.request.url, url.scheme == "https" { UIApplication.shared.open(url) }
            }
            return nil
        }

        @available(iOS 15.0, *)
        func webView(_ webView: WKWebView, requestMediaCapturePermissionFor origin: WKSecurityOrigin,
                     initiatedByFrame frame: WKFrameInfo, type: WKMediaCaptureType,
                     decisionHandler: @escaping (WKPermissionDecision) -> Void) {
            // Always ask the user; never auto-grant camera/microphone access.
            decisionHandler(origin.host.lowercased() == VOYAConstants.siteHost && origin.protocol == "https" ? .prompt : .deny)
        }
    }
}
