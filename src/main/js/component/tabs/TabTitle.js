'use strict';

import React, { Component } from 'react';
import { channelTabs, tabTitle, titleText, tabIcon } from '../../../css/tabs.css';

class TabTitle extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { text, icon } = this.props;
        let iconElement = null;
        if(icon) {
            const fontAwesomeIcon = `${tabIcon} fa ${icon}`;
            iconElement = <i className={fontAwesomeIcon} aria-hidden='true'></i>;
        }

        return (
            <div className={tabTitle}>
                {iconElement}
                <div className={titleText}>{text}</div>
            </div>
        );
    }
};

export default TabTitle;
