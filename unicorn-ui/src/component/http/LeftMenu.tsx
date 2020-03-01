import React, {Component} from "react";
import {Tabs} from 'antd';
import "antd/dist/antd.css";
import ApiList from './ApiList';

const TabPane = Tabs.TabPane;

export default class LeftMenu extends Component {

    onChangeTab = (key) => {
        console.log("点击了：" + key);
    };

    render() {
        return (
            <Tabs defaultActiveKey="1" onChange={this.onChangeTab} tabBarStyle={{textAlign: 'center'}}>
                <TabPane tab="API" key="1">
                    <ApiList/>
                </TabPane>
                <TabPane tab="History" key="2" forceRender={true}>
                    History
                </TabPane>
            </Tabs>
        )
    }
}
