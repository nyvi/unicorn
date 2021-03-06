const {override, fixBabelImports} = require('customize-cra');


const buildPath = () => (config, env) => {
    if (env === 'production') {
        const path = require('path') || "";
        const paths = require('react-scripts/config/paths');
        paths.appBuild = path.join(path.dirname(paths.appBuild), 'src/main/resources/META-INF/resources');
        config.output.path = path.join(path.dirname(config.output.path), 'src/main/resources/META-INF/resources');
    }
    return config;
};

module.exports = override(
    buildPath(),
    fixBabelImports('import', {
        libraryName: 'antd',
        libraryDirectory: 'es',
        style: 'css',
    }),
)
;