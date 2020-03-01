import React, {Component} from 'react';
import './App.css';
import "antd/dist/antd.css";
import {Tabs} from 'antd';
import ApiList from './component/http/ApiList';

const TabPane = Tabs.TabPane;

function callback(key) {
    console.log(key);
}

class App extends Component {
    render() {
        return (
            <div className="App">
                <Tabs defaultActiveKey="2" onChange={callback}>
                    <TabPane tab="Home" key="1">Home</TabPane>
                    <TabPane tab="HTTP" key="2"><ApiList/></TabPane>
                </Tabs>
            </div>
        );
    }
}

export default App;
