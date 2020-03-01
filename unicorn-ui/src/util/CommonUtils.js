/**
 * 获取请求地址
 */
export const getBasePath = () => {
    return (window.location.origin + window.location.pathname).replace("/unicorn-ui.html", "");
};