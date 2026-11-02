import SwiftUI

@main
struct VOYAApp: App {
    var body: some Scene {
        WindowGroup {
            VOYABrowserScreen()
                .preferredColorScheme(.light)
        }
    }
}
