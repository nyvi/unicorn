import React, {Component} from 'react'
import {Layout} from "antd";
import ApiList from "./ApiList";
import HttpHome from "./HttpHome";

const {Header, Sider, Content} = Layout;

export default class Docs extends Component {

    state = {
        collapsed: false,
        apiInfo: {}
    };

    toggle = () => {
        this.setState({
            collapsed: !this.state.collapsed,
        });
    };

    render() {
        const {apiInfo} = this.state;
        return (
            <Layout>
                <Sider trigger={null} collapsible collapsed={this.state.collapsed}
                       style={{overflow: 'auto', height: '100vh'}}
                       width={256}
                       theme={"light"}
                >
                    <ApiList {...apiInfo} />
                </Sider>
                <Layout className="site-layout">
                    <Header className="site-layout-background" style={{background: '#fff', padding: 0, height: 35}}>

                    </Header>
                    <Content
                        className="site-layout-background"
                        style={{
                            padding: 24,
                            minHeight: 280,
                        }}
                    >
                        <HttpHome/>
                    </Content>
                </Layout>
            </Layout>
        );
    }
}
