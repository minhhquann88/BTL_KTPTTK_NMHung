/**
 * Tải nội dung HTML từ một URL fragment và chèn vào phần tử giữ chỗ.
 * @param {string} placeholderId ID của phần tử giữ chỗ (ví dụ: 'header-placeholder').
 * @param {string} fragmentUrl Đường dẫn đến tệp fragment HTML (ví dụ: '/fragments/header.html').
 */
async function loadFragment(placeholderId, fragmentUrl) {
    const placeholder = document.getElementById(placeholderId);
    if (!placeholder) {
        console.warn(`Placeholder element with ID '${placeholderId}' not found.`);
        return;
    }
    try {
        const response = await fetch(fragmentUrl);
        if (response.ok) {
            placeholder.innerHTML = await response.text();
        } else {
            console.error(`Failed to load fragment from ${fragmentUrl}. Status: ${response.status}`);
            placeholder.innerHTML = `<p class="text-danger text-center small">Lỗi tải nội dung từ ${fragmentUrl}</p>`;
        }
    } catch (error) {
        console.error(`Error loading fragment from ${fragmentUrl}:`, error);
        placeholder.innerHTML = `<p class="text-danger text-center small">Lỗi tải nội dung từ ${fragmentUrl}</p>`;
    }
}

/**
 * Hiển thị hoặc ẩn spinner loading.
 * @param {boolean} show True để hiển thị, False để ẩn.
 * @param {string} [spinnerId='loadingSpinner'] ID của phần tử spinner.
 */
function showLoading(show, spinnerId = 'loadingSpinner') {
    const spinner = document.getElementById(spinnerId);
    if (spinner) {
        spinner.style.display = show ? 'flex' : 'none'; // Dùng flex để căn giữa nếu cần
    }
}

/**
 * Hiển thị thông báo chung (lỗi, thành công, cảnh báo).
 * @param {string} message Nội dung thông báo (có thể chứa HTML).
 * @param {'danger' | 'success' | 'warning' | 'info'} [type='danger'] Loại thông báo.
 * @param {string} [containerId='globalMessages'] ID của container chứa thông báo.
 * @param {boolean} [clearPrevious=true] Xóa các thông báo trước đó hay không.
 */
function displayGlobalMessage(message, type = 'danger', containerId = 'globalMessages', clearPrevious = true) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (clearPrevious) {
        container.innerHTML = '';
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show m-0`; // Thêm m-0 nếu cần
    alertDiv.role = 'alert';

    // Thêm icon dựa trên type
    let iconClass = 'bi-exclamation-triangle-fill'; // Mặc định cho danger
    if (type === 'success') iconClass = 'bi-check-circle-fill';
    if (type === 'warning') iconClass = 'bi-exclamation-triangle-fill';
    if (type === 'info') iconClass = 'bi-info-circle-fill';

    alertDiv.innerHTML = `
        <i class="bi ${iconClass} me-2"></i>
        <span>${message}</span>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    container.appendChild(alertDiv);
    // Cuộn lên đầu trang để người dùng thấy thông báo
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

/**
 * Định dạng số thành tiền tệ Việt Nam (VNĐ).
 * @param {number | string | null | undefined} value Giá trị số cần định dạng.
 * @returns {string} Chuỗi tiền tệ đã định dạng hoặc 'N/A'.
 */
function formatCurrency(value) {
    const numberValue = typeof value === 'string' ? parseFloat(value) : value;
    if (typeof numberValue !== 'number' || isNaN(numberValue)) {
        return 'N/A';
    }
    try {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(numberValue).replace(/\s*₫/g, ' VNĐ');
    } catch (e) {
        console.error("Error formatting currency:", e);
        return `${numberValue} VNĐ`; // Fallback đơn giản
    }
}

/**
 * Định dạng chuỗi ngày giờ ISO (hoặc đối tượng Date) thành dd/MM/yyyy HH:mm.
 * @param {string | Date | null | undefined} dateTimeInput Chuỗi ngày giờ hoặc đối tượng Date.
 * @returns {string} Chuỗi ngày giờ đã định dạng hoặc 'N/A'.
 */
function formatLocalDateTime(dateTimeInput) {
    if (!dateTimeInput) return 'N/A';
    try {
        const date = new Date(dateTimeInput);
        // Kiểm tra xem Date có hợp lệ không
        if (isNaN(date.getTime())) {
            return 'N/A';
        }
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0'); // Tháng bắt đầu từ 0
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${day}/${month}/${year} ${hours}:${minutes}`;
    } catch (e) {
        console.error("Error formatting date/time:", e);
        return 'N/A';
    }
}

/**
 * Định dạng chuỗi ngày ISO (yyyy-MM-dd) thành dd/MM/yyyy.
 * @param {string | null | undefined} dateString Chuỗi ngày yyyy-MM-dd.
 * @returns {string} Chuỗi ngày đã định dạng hoặc 'N/A'.
 */
function formatDateForDisplay(dateString) {
    if (!dateString || !/^\d{4}-\d{2}-\d{2}$/.test(dateString)) return 'N/A';
    try {
        const [year, month, day] = dateString.split('-');
        return `${day}/${month}/${year}`;
    } catch (e) {
        console.error("Error formatting date:", e);
        return 'N/A';
    }
}

// Tự động tải header và footer khi DOM sẵn sàng
document.addEventListener('DOMContentLoaded', () => {
    loadFragment('header-placeholder', '/fragments/header.html');
    // Tìm footer placeholder và tải fragment
    const footerPlaceholder = document.getElementById('footer-placeholder');
    if (footerPlaceholder) {
        loadFragment('footer-placeholder', '/fragments/footer.html');
    } else {
        console.warn("Footer placeholder 'footer-placeholder' not found on this page.");
    }
});
