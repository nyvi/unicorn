import React, {Component} from 'react';
import './App.css';

import {Menu} from 'antd';

const {SubMenu} = Menu;

class App extends Component {

    state = {
        docMap: {}
    };

    componentDidMount() {
        this.getDoc((res) => {
            this.setState({docMap: res});
        });
    };

    handleClick = () => {
        // this.getDoc();
        console.log("点击了")
    };

    getDoc = (callBack) => {
        let basePath = (window.location.origin + window.location.pathname).replace("/unicorn-ui.html", "");
        let docUrl = basePath + "/unicorn/api-docs";
        fetch(docUrl, {
            method: 'GET',
            headers: new Headers({'Content-Type': 'application/json'})
        }).then(
            res => res.json().then((data) => {
                callBack(data);
            })
        )
    };

    render() {
        const {docMap} = this.state;

        const loop = data => {
            let ret = [];
            for (let key in data) {
                if (!data.hasOwnProperty(key)) continue;
                let value = data[key];
                let name = value.desc ? value.desc : value.name;
                let api = [];
                let apiList = value.apiList;
                for (let i = 0; i < apiList.length; i++) {
                    api.push(
                        <Menu.Item key={apiList[i].path} title={apiList[i].path}>
                            {apiList[i].desc ? apiList[i].desc : apiList[i].path}
                        </Menu.Item>
                    )
                }
                ret.push(<SubMenu title={name} key={key}>{api}</SubMenu>);
            }
            return ret;
        };

        return (
            <Menu
                onClick={this.handleClick}
                style={{width: 256, height: '100%'}}
                // defaultSelectedKeys={['1']}
                // defaultOpenKeys={['sub1']}
                mode="inline"
            >
                {loop(docMap)}
            </Menu>
        );
    }
}

export default App;