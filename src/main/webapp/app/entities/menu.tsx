import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/point">
        <Translate contentKey="global.menu.entities.point" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/board">
        <Translate contentKey="global.menu.entities.board" />
      </MenuItem>
    </>
  );
};

export default EntitiesMenu;
