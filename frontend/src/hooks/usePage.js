import { useContext } from 'react';
import { PageContext } from '../contexts/PageContext';

export const usePage = () => useContext(PageContext);
