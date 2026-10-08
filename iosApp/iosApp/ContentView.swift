import CCPSample
import SwiftUI

/// Hosts the shared Compose demo (sample/ → `SampleApp()`) inside SwiftUI. `MainViewController()`
/// is a Kotlin top-level function in MainViewController.kt, so Swift reaches it through the
/// generated `MainViewControllerKt` class.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        // Compose draws edge-to-edge and handles the safe area itself (statusBarsPadding & co).
        ComposeView()
            .ignoresSafeArea()
    }
}
