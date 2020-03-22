import React, {Component} from 'react';
import {Menu} from 'antd';
import {getBasePath} from "../../util/CommonUtils";

const {SubMenu} = Menu;

export default class ApiList extends Component {
    state = {
        docMap: {},
        apiInfo: {}
    };

    constructor(props) {
        super(props);
        const {apiInfo} = props;
        this.setState({apiInfo: apiInfo});
    };

    componentDidMount() {
        this.getDoc((res) => {
            this.setState({docMap: res});
        });
    };

    handleClick = e => {
        const {docMap} = this.state;
        const key = e.keyPath[1];
        const apiList = docMap[key]["apiList"]
        for (let i in apiList) {
            if (!apiList.hasOwnProperty(i)) continue;
            if (e.key === apiList[i]["path"]) {
                this.setState({apiInfo: apiList[i]});
                break;
            }
        }
    };

    getDoc = (callBack) => {
        let basePath = getBasePath();
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
                mode="inline"
            >
                {loop(docMap)}
            </Menu>
        );
    }
}