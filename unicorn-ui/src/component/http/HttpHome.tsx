import React, {Component} from "react";
import {Button, Col, Input, Row, Spin, Tabs} from "antd";

export default class HttpHome extends Component {

    state = {
        appInfo: {
            url: "/test"
        }
    };


    render() {
        const TabPane = Tabs.TabPane;
        let appInfo = this.state.appInfo;
        return (
            <div className="site-layout-background">
                <Spin tip="Loading..." spinning={false}>
                    <Row>
                        <Col span={8} style={{marginLeft: '20px', width: '50%'}}>
                            <Input value={appInfo.url} disabled={false}/>
                        </Col>
                        <Col span={8} style={{textAlign: 'center', padding: '0 20px 0 20px'}}>
                            <Button type="primary">Send</Button>
                        </Col>
                    </Row>
                    <Tabs>
                        <TabPane tab="Mock" key="1">
                            Mock
                        </TabPane>
                        <TabPane tab="文档" key="2">
                            文档
                        </TabPane>
                    </Tabs>
                </Spin>
            </div>
        );
    }
}