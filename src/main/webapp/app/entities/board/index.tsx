import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Board from './board';
import BoardDetail from './board-detail';
import BoardUpdate from './board-update';
import BoardDeleteDialog from './board-delete-dialog';

const BoardRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Board />} />
    <Route path="new" element={<BoardUpdate />} />
    <Route path=":id">
      <Route index element={<BoardDetail />} />
      <Route path="edit" element={<BoardUpdate />} />
      <Route path="delete" element={<BoardDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BoardRoutes;
