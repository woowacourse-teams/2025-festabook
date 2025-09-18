import SwiftUI

struct LostFoundListView: View {
    @ObservedObject var viewModel: LostItemViewModel
    
    var body: some View {
        LostItemGridView(viewModel: viewModel)
    }
}
